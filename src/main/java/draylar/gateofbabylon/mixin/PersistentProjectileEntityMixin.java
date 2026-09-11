package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.ProjectileManipulator;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stores the source bow stack on vanilla arrows for the mod's projectile API. */
@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin extends Entity implements ProjectileManipulator {

    private static final EntityDataAccessor<ItemStack> ORIGIN_STACK =
            SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.ITEM_STACK);

    private PersistentProjectileEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void setOrigin(ItemStack stack) {
        entityData.set(ORIGIN_STACK, stack.copy());
    }

    @Override
    public ItemStack getOrigin() {
        return entityData.get(ORIGIN_STACK);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void addDataTrackers(CallbackInfo callbackInfo) {
        entityData.define(ORIGIN_STACK, ItemStack.EMPTY);
    }
}
