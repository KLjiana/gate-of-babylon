package draylar.gateofbabylon.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.UUID;

public class DaggerItem extends SwordItem implements EnchantmentHandler {

    public static final UUID REDUCTION_UUID = UUID.fromString("7b0363d1-7818-44cc-a605-b2847a065548");

    public DaggerItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamageBonus() - 1), -4 + effectiveSpeed, settings);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        Multimap<Attribute, AttributeModifier> modifiers = super.getDefaultAttributeModifiers(slot);
        builder.putAll(modifiers);

        return builder.build();
    }
}

