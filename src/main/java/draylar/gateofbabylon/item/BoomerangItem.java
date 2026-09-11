package draylar.gateofbabylon.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BoomerangItem extends TieredItem implements EnchantmentHandler {

    private final Tier material;
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public BoomerangItem(Item.Properties settings, Tier material) {
        super(material, settings);
        this.material = material;

        this.attackDamage = 3 + material.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("fa2339c3-5e3e-4c7d-8f7c-0a4f0f7db4f2"), "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        // If the user already has a boomerang out, do not allow a new one
        List<BoomerangEntity> found = new ArrayList<>(world.getEntitiesOfClass(
                BoomerangEntity.class,
                new AABB(user.blockPosition().offset(-25, -25, -25), user.blockPosition().offset(25, 25, 25)),
                boomerang -> boomerang.isAlive() && boomerang.getOwner().isPresent() && boomerang.getOwner().get().equals(user.getUUID())));

        // Boomerang was found, remove it and stop early.
        if(!found.isEmpty()) {
            return InteractionResultHolder.fail(user.getItemInHand(hand));
        }

        if(!world.isClientSide) {
            BoomerangEntity boomerang = createBoomerang(user.getItemInHand(hand), world);
            boomerang.setYRot(user.getYRot());
            boomerang.setXRot(user.getXRot());
            boomerang.setDeltaMovement(boomerang.getViewVector(1.0F));
            double y = user.getEyeY() - .2;
            boomerang.setPos(user.getX(), y, user.getZ());
            boomerang.setPos(user.getX(), y, user.getZ());
            boomerang.setPos(user.getX(), y, user.getZ());
            boomerang.setOwner(user);
            world.addFreshEntity(boomerang);
        }

        return InteractionResultHolder.success(user.getItemInHand(hand));
    }

    public BoomerangEntity createBoomerang(ItemStack stack, Level world) {
        BoomerangEntity boomerang = new BoomerangEntity(GOBEntities.BOOMERANG.get(), world);
        boomerang.setStack(stack);
        return boomerang;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);

        float attackDamage = ((BoomerangItem) stack.getItem()).getMaterial().getAttackDamageBonus() + EnchantmentHelper.getDamageBonus(stack, null);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if(slot.equals(EquipmentSlot.MAINHAND)) {
            return attributeModifiers;
        } else {
            return ImmutableMultimap.of();
        }
    }

    public Tier getMaterial() {
        return material;
    }

    @Override
    public List<EnchantmentCategory> getEnchantmentTypes() {
        return Collections.singletonList(EnchantmentCategory.WEAPON);
    }

    @Override
    public boolean isExplicitlyValid(Enchantment enchantment) {
        return enchantment.equals(Enchantments.PIERCING);
    }
}

