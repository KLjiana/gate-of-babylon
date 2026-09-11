package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.item.BoomerangItem;
import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class BoomerangEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(BoomerangEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(BoomerangEntity.class, EntityDataSerializers.ITEM_STACK);
    private int lastLeverAge = 0;

    // Data for temporary boomerangs (dispensers or other mechanics that shoot a Boomerang which only retracts once)
    private boolean isTemporary = false;
    private boolean hasTemporaryReturned = false;
    private Vec3 temporaryOrigin = Vec3.ZERO;

    // TODO: MAX PIERCING ENTITIES?

    public BoomerangEntity(EntityType<BoomerangEntity> type, Level world) {
        super(type, world);
    }
    public BoomerangEntity(Level world, double x, double y, double z) {
        super(GOBEntities.BOOMERANG.get(), world);
        this.setPos(x, y, z);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        // :)
        if (!level().isClientSide) {
            if(tickCount % 5 == 0) {
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 0.5f, .5f);
            }

            hasImpulse = true;

            move(MoverType.SELF, getDeltaMovement());

            // When the boomerang approaches the return time (1 second, 20 ticks), it will slow down.
            if(tickCount % 20 >= 15) {
                int t = 20 - (tickCount % 20);
                double modifier =  t / 5f;
                setDeltaMovement(getDeltaMovement().scale(modifier));
            }

            // Every second, the boomerang will redirect back towards the player.
            if(tickCount % 20 == 0) {
                // turn towards user every tick
                if(getOwner().isPresent()) {
                    Player owner = level().getPlayerByUUID(getOwner().get());

                    if(owner != null) {
                        Vec3 ownerPos = owner.position();
                        ownerPos = ownerPos.multiply(1.0D, 0.0D, 1.0D).add(0, owner.getEyeY() - .2, 0);
                        Vec3 thisPos = position();
                        Vec3 difference = ownerPos.subtract(thisPos);
                        setDeltaMovement(difference.normalize());
                    } else {
                        remove(RemovalReason.DISCARDED);
                    }
                } else if (isTemporary) {
                    if(!hasTemporaryReturned) {
                        Vec3 thisPos = position();
                        Vec3 difference = temporaryOrigin.subtract(thisPos);
                        setDeltaMovement(difference.normalize());
                        hasTemporaryReturned = true;
                    } else {
                        remove(RemovalReason.DISCARDED);
                    }
                } else {
                    remove(RemovalReason.DISCARDED);
                }
            }

            // delete after 10 seconds to prevent glitche
            if(tickCount > 200) {
                remove(RemovalReason.DISCARDED);
            }
        }

        // collision
        if (!level().isClientSide) {
            level().getEntitiesOfClass(LivingEntity.class, new AABB(getX() - .4f, getY() - .05f, getZ() - .4f, getX() + .4f, getY() + .05f, getZ() + .4f), entity -> true).forEach(this::onCollision);

            BlockPos insidePos = blockPosition();
            BlockPos towardsPos = BlockPos.containing(position().add(getDeltaMovement().normalize()));
            BlockState insideState = level().getBlockState(blockPosition());
            BlockState towardsState = level().getBlockState(towardsPos);

            // Play collision sounds based on the block the Boomerang is flying into.
            if (!towardsState.isAir() && towardsState.getFluidState().isEmpty()) {
                level().playSound(null, getX(), getY(), getZ(), towardsState.getSoundType().getHitSound(), SoundSource.PLAYERS, 0.5f, 1.0f);
            }

            // If the boomerang is inside a button, press it.
            if(insideState.getBlock() instanceof ButtonBlock button) {
                if (!insideState.getValue(ButtonBlock.POWERED)) {
                    button.press(insideState, level(), insidePos);
                    level().playSound(null, insidePos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
                    level().gameEvent(this, GameEvent.BLOCK_ACTIVATE, insidePos);
                }
            }

            // Flip levers!
            int timeSinceLastLever = tickCount - lastLeverAge;
            if((lastLeverAge == 0 || timeSinceLastLever >= 20) && insideState.getBlock() instanceof LeverBlock lever) {
                lever.pull(insideState, level(), insidePos);
                float f = insideState.getValue(LeverBlock.POWERED) ? 0.6F : 0.5F;
                level().playSound(null, insidePos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
                level().gameEvent(this, insideState.getValue(LeverBlock.POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, insidePos);
                lastLeverAge = tickCount;
            }

            // If the boomerang is inside a replaceable block (such as grass), break it.
            if (insideState.canBeReplaced() && !insideState.isAir() && insideState.getFluidState().isEmpty()) {
                level().destroyBlock(insidePos, true, this, 512);
            }
        }
    }

    public void onCollision(LivingEntity entity) {
        ItemStack stack = getStack();

        if(getOwner().isPresent() && entity.getUUID().equals(getOwner().get()) && tickCount > 3) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.25f, 1.0f);
            remove(RemovalReason.DISCARDED);
            return;
        } else if (getOwner().isPresent() && entity.getUUID().equals(getOwner().get())) {
            return;
        }

        if(stack.getItem() instanceof BoomerangItem) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 0.5f, 1.0f);
            float baseDamage = ((BoomerangItem) stack.getItem()).getMaterial().getAttackDamageBonus();

            // Check if the player is valid for attack damage calculations.
            if(getOwner().isPresent()) {
                Player player = level().getPlayerByUUID(getOwner().get());

                // If the player is valid, overwrite the material damage with our generic attack damage attribute.
                if(player != null) {
                    baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                }
            }

            // calculate final damage with enchantments and attack entity
            float attackDamage = baseDamage + EnchantmentHelper.getDamageBonus(stack, entity.getMobType());
            boolean dmg;
            if(getOwner().isPresent() && level().getPlayerByUUID(getOwner().get()) != null) {
                dmg = entity.hurt(damageSources().playerAttack(level().getPlayerByUUID(getOwner().get())), attackDamage);

                // damage boomerang stack
                Player owner = level().getPlayerByUUID(getOwner().get());
                if (owner != null) {
                    stack.hurtAndBreak(1, owner, playerEntity -> playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                }
            } else {
                dmg = entity.hurt(damageSources().generic(), attackDamage);
            }

            // Apply fire aspect
            int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, getStack());
            if (level > 0) {
                entity.setSecondsOnFire(4 * level);
            }

            // do not interact when hitting the source player
            if(getOwner().isEmpty() || !entity.getUUID().equals(getOwner().get())) {
                int piercing = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, getStack());

                // knock back
                if(piercing == 0 && dmg) {
                    entity.setDeltaMovement(getDeltaMovement());
                }

                // if we hit an entity and the boomerang does not have piercing, return back
                if (piercing == 0) {
                    Player owner = getOwner().isEmpty() ? null : level().getPlayerByUUID(getOwner().get());

                    if(owner != null) {
                        Vec3 ownerPos = owner.position();
                        ownerPos = ownerPos.multiply(1.0D, 0.0D, 1.0D).add(0, owner.getEyeY() - .2, 0);
                        Vec3 thisPos = position();
                        Vec3 difference = ownerPos.subtract(thisPos);
                        setDeltaMovement(difference.normalize());
                    } else {
                        remove(RemovalReason.DISCARDED);
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER, Optional.empty());
        entityData.define(STACK, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {

    }

    public void setStack(ItemStack stack) {
        entityData.set(STACK, stack);
    }

    public ItemStack getStack() {
        return entityData.get(STACK);
    }

    public void setOwner(@NotNull Player player) {
        entityData.set(OWNER, Optional.of(player.getUUID()));
    }

    @NotNull
    public Optional<UUID> getOwner() {
        return entityData.get(OWNER);
    }

    public Optional<Player> getPlayerOwner() {
        // can we condense this
        return getOwner().isPresent() && level().getPlayerByUUID(getOwner().get()) != null ? Optional.ofNullable(level().getPlayerByUUID(getOwner().get())) : Optional.empty();
    }

    public void setTemporary() {
        isTemporary = true;
        temporaryOrigin = position();
    }
}

