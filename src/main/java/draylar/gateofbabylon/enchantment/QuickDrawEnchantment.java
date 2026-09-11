package draylar.gateofbabylon.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;

public class QuickDrawEnchantment extends Enchantment {

    public QuickDrawEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BOW, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}

