package draylar.gateofbabylon.enchantment;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import draylar.gateofbabylon.item.WaraxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class SmashingEnchantment extends Enchantment implements ValidatingEnchantment {

    public SmashingEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return other != Enchantments.MOB_LOOTING;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof WaraxeItem;
    }
}

