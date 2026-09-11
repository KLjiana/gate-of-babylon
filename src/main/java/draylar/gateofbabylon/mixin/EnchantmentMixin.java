package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.EnchantmentHandler;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow @Final public EnchantmentCategory category;

    @Inject(
            method = "canEnchant",
            at = @At("HEAD"),
            cancellable = true)
    private void checkValidity(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // If the stack being checked is from this mod, run our custom logic to determine
        // whether the given type is valid.
        if(stack.getItem() instanceof EnchantmentHandler) {
            // Check for explicit whitelist
            if(((EnchantmentHandler) stack.getItem()).isExplicitlyValid((Enchantment) (Object) this)) {
                cir.setReturnValue(true);
                return;
            }

            // Check for type-validity
            boolean contains = ((EnchantmentHandler) stack.getItem()).getEnchantmentTypes().contains(category);
            boolean itemAccepts = !((EnchantmentHandler) stack.getItem()).isInvalid((Enchantment) (Object) this);

            // Only abort-mission early if we deem the stack to be valid for this enchantment.
            // This allows for the default logic to attempt to run as well.
            if(contains && itemAccepts) {
                cir.setReturnValue(true);
            }
        }
    }
}

