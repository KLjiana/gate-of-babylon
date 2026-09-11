package draylar.gateofbabylon.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SpearItem extends TieredItem implements EnchantmentHandler {

    public static final UUID INCREASE_UUID = UUID.fromString("7b0363d1-7818-44cc-a605-b2847a065548");
    private final float effectiveDamage;
    private final float effectiveSpeed;

    public SpearItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, settings);

        this.effectiveDamage = effectiveDamage - 1;
        this.effectiveSpeed = -4 + effectiveSpeed;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player) {
            Player player = (Player) user;
            int currentUseTime = this.getUseDuration(stack) - remainingUseTicks;

            if (currentUseTime >= 10) {
                if (!world.isClientSide) {
                    stack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(user.getUsedItemHand()));

                    // Create initial Spear entity
                    SpearProjectileEntity spearEntity = new SpearProjectileEntity(world, player, stack);
                    spearEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                    if (player.getAbilities().instabuild) {
                        spearEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    world.addFreshEntity(spearEntity);

                    // Play SFX
                    world.playSound(null, spearEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

                    // Remove Spear from inventory after throw
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().removeItem(stack);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public List<EnchantmentCategory> getEnchantmentTypes() {
        return Arrays.asList(EnchantmentCategory.WEAPON, EnchantmentCategory.TRIDENT);
    }

    @Override
    public boolean isInvalid(Enchantment enchantment) {
        return enchantment == Enchantments.SWEEPING_EDGE;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        Multimap<Attribute, AttributeModifier> modifiers = super.getDefaultAttributeModifiers(slot);
        builder.putAll(modifiers);

        if(slot == EquipmentSlot.MAINHAND) {
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", effectiveDamage, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", effectiveSpeed, AttributeModifier.Operation.ADDITION));
        }

        return builder.build();
    }
}
