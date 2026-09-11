package draylar.gateofbabylon.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Defines an {@link net.minecraft.world.item.enchantment.Enchantment} that approves items through {@link net.minecraft.world.item.enchantment.Enchantment#isAcceptableItem(ItemStack)}
 *    rather than {@link net.minecraft.world.item.enchantment.EnchantmentCategory#isAcceptableItem(Item)} in enchantment tables.
 */
public interface ValidatingEnchantment {
}

