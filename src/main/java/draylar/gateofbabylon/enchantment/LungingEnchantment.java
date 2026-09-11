package draylar.gateofbabylon.enchantment;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import draylar.gateofbabylon.item.RapierItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class LungingEnchantment extends Enchantment implements ValidatingEnchantment {

    public LungingEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[] {
                EquipmentSlot.MAINHAND
        });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof RapierItem;
    }
}

