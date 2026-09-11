package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.item.YoyoItem;
import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class YoyoEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(YoyoEntity.class, EntityDataSerializers.ITEM_STACK);

    public YoyoEntity(EntityType<YoyoEntity> type, Level world) {
        super(type, world);
    }
    public YoyoEntity(Level world, double x, double y, double z) {
        super(GOBEntities.YOYO.get(), world);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        // :)
        if(!level().isClientSide) {
            if(entityData.get(OWNER).isPresent()) {
                Player owner = level().getPlayerByUUID(entityData.get(OWNER).get());

                if (owner != null) {
                    HitResult ray = owner.pick(5, 0, false);
                    Vec3 targetPos = ray.getLocation();
                    Vec3 thisPos = position();

                    double distance = targetPos.distanceTo(thisPos);
                    Vec3 difference = targetPos.subtract(thisPos).normalize().scale(Math.min(distance, 1));

                    setDeltaMovement(difference);
                    hasImpulse = true;
                }
            }

            move(MoverType.SELF, getDeltaMovement());
        }

        // collision
        if(!level().isClientSide) {
            level().getEntitiesOfClass(LivingEntity.class, new AABB(getX() - .25f, getY() - .25f, getZ() - .25f, getX() + .25f, getY() + .25f, getZ() + .25f), entity -> true).forEach(this::onCollision);

            // calculate distance between player and yoyo
            if(getOwner().isPresent()) {
                Player owner = level().getPlayerByUUID(getOwner().get());

                if(owner != null) {
                    Vec3 rotationVector = owner.getViewVector(1.0F);
                    Vec3 yoyoPosition = position();
                    Vec3 target = yoyoPosition.add(rotationVector);

                    BlockPos p = BlockPos.containing(target);
                    BlockState blockState = level().getBlockState(p);
                    if(!blockState.isAir()) {
                        level().playSound(null, getX(), getY(), getZ(), blockState.getSoundType().getHitSound(), SoundSource.PLAYERS, 0.5f, 1.0f);
                    }

                    if(blockState.canBeReplaced()) {
                        level().destroyBlock(p, true, this, 512);
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

    public void setOwner(@NotNull Player player) {
        entityData.set(OWNER, Optional.of(player.getUUID()));
    }

    @NotNull
    public Optional<UUID> getOwner() {
        return entityData.get(OWNER);
    }

    public void onCollision(LivingEntity entity) {
        ItemStack stack = getStack();

        // do not collide with other
        if(getOwner().isPresent() && entity.getUUID().equals(getOwner().get())) {
            return;
        }

        if(stack.getItem() instanceof YoyoItem) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 0.5f, 1.0f);
            float attackDamage = ((YoyoItem) stack.getItem()).getMaterial().getAttackDamageBonus() + EnchantmentHelper.getDamageBonus(stack, entity.getMobType());

            if(getOwner().isPresent() && level().getPlayerByUUID(getOwner().get()) != null) {
                entity.hurt(damageSources().playerAttack(level().getPlayerByUUID(getOwner().get())), attackDamage);

                // damage yoyo
                Player owner = level().getPlayerByUUID(getOwner().get());
                if (owner != null) {
                    stack.hurtAndBreak(1, owner, player -> player.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                }
            } else {
                entity.hurt(damageSources().generic(), attackDamage);
            }

            // Apply fire aspect
            int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, getStack());
            if (level > 0) {
                entity.setSecondsOnFire(4 * level);
            }

            // knock back
            entity.setDeltaMovement(getDeltaMovement());
        }
    }

    public void setStack(ItemStack stack) {
        entityData.set(STACK, stack);
    }

    public ItemStack getStack() {
        return entityData.get(STACK);
    }

    public void retract() {

    }

    public void deploy() {

    }
}

