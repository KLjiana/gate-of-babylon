package draylar.gateofbabylon.api;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Collections;
import java.util.List;

public interface EnchantmentHandler {
    default List<EnchantmentCategory> getEnchantmentTypes() {
        return Collections.emptyList();
    }

    default boolean isInvalid(Enchantment enchantment) {
        return false;
    }

    default boolean isExplicitlyValid(Enchantment enchantment) { return false; }
}

