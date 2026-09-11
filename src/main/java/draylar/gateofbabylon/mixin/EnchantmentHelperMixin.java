package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.EnchantmentHandler;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(
            method = "getAvailableEnchantmentResults",
            at = @At("HEAD"), cancellable = true)
    private static void adjustPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        Item item = stack.getItem();

        // Only adjust if the stack being passed in is our weapon
        if (item instanceof EnchantmentHandler) {
            List<EnchantmentInstance> entries = new java.util.ArrayList<>();

            // Collect valid enchantments
            BuiltInRegistries.ENCHANTMENT.forEach(enchantment -> {

                // Items can whitelist certain enchantments to always be valid.
                if(!((EnchantmentHandler) item).isExplicitlyValid(enchantment)) {

                    // This is where our primary logic-change is.
                    // Instead of asking the type for validity, we ask the enchantment.
                    // This allows our other hook in EnchantmentMixin to run.
                    if(!enchantment.canEnchant(stack)) {
                        return;
                    }

                    // Ensure the stack accepts the given enchantment.
                    if(((EnchantmentHandler) item).isInvalid(enchantment)) {
                        return;
                    }

                    // If the enchantment is not available in the general pool (Soul Speed),
                    // ignore it.
                    if(!enchantment.isDiscoverable()) {
                        return;
                    }

                    // If the enchantment is a treasure enchantment
                    //  and we are not looking for treasure enchantments, ignore it.
                    if(enchantment.isTreasureOnly() && !treasureAllowed) {
                        return;
                    }
                }

                // Add all valid enchantment-power entries to the list.
                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (power >= enchantment.getMinCost(i) && power <= enchantment.getMaxCost(i)) {
                        entries.add(new EnchantmentInstance(enchantment, i));
                        break;
                    }
                }
            });

            cir.setReturnValue(entries);
        }
    }
}

