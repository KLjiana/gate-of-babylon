package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpearProjectileEntity extends AbstractArrow {

    private static final EntityDataAccessor<Byte> LOYALTY = SynchedEntityData.defineId(SpearProjectileEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ENCHANTED = SynchedEntityData.defineId(SpearProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(SpearProjectileEntity.class, EntityDataSerializers.ITEM_STACK);

    private ItemStack stack = ItemStack.EMPTY;
    private boolean dealtDamage;
    public int returnTimer;

    public SpearProjectileEntity(EntityType<SpearProjectileEntity> entityType, Level level) {
        super(entityType, level);
        this.stack = new ItemStack(Items.TRIDENT);
    }

    public SpearProjectileEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(GOBEntities.SPEAR.get(), owner, level);
        this.stack = stack.copy();
        this.entityData.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
        this.entityData.set(ENCHANTED, stack.hasFoil());
        this.entityData.set(STACK, stack.copy());
    }

    public SpearProjectileEntity(Level level, double x, double y, double z) {
        super(GOBEntities.SPEAR.get(), x, y, z, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOYALTY, (byte) 0);
        this.entityData.define(ENCHANTED, false);
        this.entityData.define(STACK, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity owner = this.getOwner();
        if ((this.dealtDamage || this.isNoPhysics()) && owner != null) {
            int loyalty = this.entityData.get(LOYALTY);
            if (loyalty > 0 && !this.isOwnerAlive()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.asItemStack(), 0.1F);
                }
                this.discard();
            } else if (loyalty > 0) {
                this.setNoPhysics(true);
                Vec3 difference = new Vec3(owner.getX() - this.getX(), owner.getEyeY() - this.getY(), owner.getZ() - this.getZ());
                this.setPos(this.getX(), this.getY() + difference.y * 0.015D * loyalty, this.getZ());
                double speed = 0.05D * loyalty;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(difference.normalize().scale(speed)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }
                ++this.returnTimer;
            }
        }
        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity owner = this.getOwner();
        return owner != null && owner.isAlive() && (!(owner instanceof ServerPlayer serverPlayer) || !serverPlayer.isSpectator());
    }

    public ItemStack asItemStack() {
        return this.stack.copy();
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.asItemStack();
    }

    public ItemStack getStack() {
        return this.level().isClientSide ? this.entityData.get(STACK) : this.stack;
    }

    public boolean isEnchanted() {
        return this.entityData.get(ENCHANTED);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        float damage = 8.0F;
        if (target instanceof LivingEntity livingTarget) {
            damage += EnchantmentHelper.getDamageBonus(this.stack, livingTarget.getMobType());
        }

        Entity spearOwner = this.getOwner();
        this.dealtDamage = true;
        DamageSource damageSource = this.damageSources().trident(this, spearOwner == null ? this : spearOwner);
        SoundEvent hitSound = SoundEvents.TRIDENT_HIT;
        if (target.hurt(damageSource, damage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (target instanceof LivingEntity livingTarget && spearOwner instanceof LivingEntity livingOwner) {
                EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
                EnchantmentHelper.doPostDamageEffects(livingOwner, livingTarget);
            }
        }

        int fireAspectLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, this.stack);
        if (fireAspectLevel > 0) {
            target.setSecondsOnFire(fireAspectLevel * 4);
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float impactVolume = 1.0F;
        if (this.level() instanceof ServerLevel serverLevel && serverLevel.isThundering()
                && EnchantmentHelper.hasChanneling(this.stack)) {
            BlockPos blockPos = target.blockPosition();
            if (serverLevel.getBrightness(LightLayer.SKY, blockPos) > 0) {
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightning != null) {
                    lightning.moveTo(Vec3.atBottomCenterOf(blockPos));
                    lightning.setCause(spearOwner instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    serverLevel.addFreshEntity(lightning);
                    hitSound = SoundEvents.TRIDENT_THUNDER;
                    impactVolume = 5.0F;
                }
            }
        }
        this.playSound(hitSound, impactVolume, 1.0F);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Stack", 10)) {
            this.stack = ItemStack.of(tag.getCompound("Stack"));
        }
        this.dealtDamage = tag.getBoolean("DealtDamage");
        this.entityData.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(this.stack));
        this.entityData.set(STACK, this.stack.copy());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Stack", this.stack.save(new CompoundTag()));
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}
