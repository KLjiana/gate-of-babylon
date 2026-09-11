package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.DoubleAttackHelper;
import draylar.gateofbabylon.item.CustomShieldItem;
import draylar.gateofbabylon.item.HaladieItem;
import draylar.gateofbabylon.registry.GOBItems;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.TimerTask;
import java.util.function.Consumer;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract void awardStat(Stat<?> stat);

    @Shadow public abstract ItemCooldowns getCooldowns();

    @Shadow public abstract void attack(Entity target);

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "hurtCurrentlyUsedShield",
            at = @At("HEAD"),
            cancellable = true
    )
    private void damageCustomShield(float amount, CallbackInfo ci) {
        if (this.useItem.getItem() instanceof CustomShieldItem) {

            // Increment 'used' stat for the current shield item on server
            if (!level().isClientSide) {
                awardStat(Stats.ITEM_USED.get(this.useItem.getItem()));
            }

            // Only reduce shield durability if the incoming damage is greater than 3
            if (amount >= 3.0F) {
                int trueDamage = 1 + Mth.floor(amount);
                InteractionHand activeHand = this.getUsedItemHand();
                this.useItem.hurtAndBreak(trueDamage, (Player) (Object) this, playerEntity -> playerEntity.broadcastBreakEvent(activeHand)); // Damage held stack

                // Play FX
                if (this.useItem.isEmpty()) {
                    if (activeHand == InteractionHand.MAIN_HAND) {
                        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    useItem = ItemStack.EMPTY;
                    playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + level().random.nextFloat() * 0.4F);
                }
            }
        }
    }

    @Inject(
            method = "disableShield",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V")
    )
    public void disableShield(boolean sprinting, CallbackInfo ci) {
        this.getCooldowns().addCooldown(GOBItems.STONE_SHIELD.get(), 100);
        this.getCooldowns().addCooldown(GOBItems.IRON_SHIELD.get(), 100);
        this.getCooldowns().addCooldown(GOBItems.GOLDEN_SHIELD.get(), 100);
        this.getCooldowns().addCooldown(GOBItems.DIAMOND_SHIELD.get(), 100);
        this.getCooldowns().addCooldown(GOBItems.NETHERITE_SHIELD.get(), 100);
    }

    @Unique
    private boolean gob_hasHaladieAttacked = false;

    @Inject(
            method = "attack",
            at = @At("RETURN"))
    private void onAttack(Entity target, CallbackInfo ci) {
        // If we are holding a Haladie, enter double-attack logic.
        if(getMainHandItem().getItem() instanceof HaladieItem && !level().isClientSide) {
            // If we have NOT already attacked, reset the enemies i-frames and attack again.
            if(!gob_hasHaladieAttacked) {
                target.invulnerableTime = 0;
                gob_hasHaladieAttacked = true;
                DoubleAttackHelper.queueDoubleAttack((ServerPlayer) (Object) this, target);
                return;
            }
        }

        gob_hasHaladieAttacked = false;
    }
}

