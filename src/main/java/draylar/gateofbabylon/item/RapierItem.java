package draylar.gateofbabylon.item;

import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.api.LungeManipulator;
import draylar.gateofbabylon.registry.GOBEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class RapierItem extends SwordItem implements EnchantmentHandler {

    public RapierItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamageBonus() - 1), -4 + effectiveSpeed, settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if(((LungeManipulator) user).gateOfBabylon$canLunge()) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 0.05F, 1.75F / (world.random.nextFloat() * 0.4F + 0.8F));
            user.getCooldowns().addCooldown(this, 40);

            if (!world.isClientSide) {
                // Apply the dash from the server so the player's authoritative position moves.
                // Explicitly mark the motion for synchronization; this is important for players,
                // whose client otherwise continues to predict the old movement.
                int lungingLevel = EnchantmentHelper.getItemEnchantmentLevel(GOBEnchantments.LUNGING, itemStack);
                double bonus = 1.0D + lungingLevel * 0.3D;
                Vec3 lunge = user.getLookAngle().scale(bonus);
                user.setDeltaMovement(user.getDeltaMovement().add(lunge));
                user.hasImpulse = true;
                user.hurtMarked = true;
            }

            ((LungeManipulator) user).gateOfBabylon$setLunged();
            user.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.pass(itemStack);
    }
}

