package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class BroadswordItem extends SwordItem implements EnchantmentHandler {

    public BroadswordItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamageBonus()), -4 + effectiveSpeed, settings);
    }
}

