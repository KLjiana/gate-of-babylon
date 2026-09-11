package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.item.CustomShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {

    private MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    // Replicate Vanilla shield-disabling behavior for custom shield items (vanilla directly checks against shield item instance)
    @Inject(
            method = "maybeDisableShield",
            at = @At("HEAD")
    )
    private void disableCustomShield(Player player, ItemStack mobStack, ItemStack playerStack, CallbackInfo ci) {
        if(!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.getItem() instanceof AxeItem && playerStack.getItem() instanceof CustomShieldItem) {
            float efficiency = 0.25F + (float) EnchantmentHelper.getBlockEfficiency((Mob) (Object) this) * 0.05F;

            if (this.random.nextFloat() < efficiency) {
                player.getCooldowns().addCooldown(Items.SHIELD, 100);
                level().broadcastEntityEvent(player, (byte) 30);
            }
        }
    }
}

