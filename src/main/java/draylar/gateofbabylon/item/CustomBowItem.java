package draylar.gateofbabylon.item;

import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.api.ProjectileManipulator;
import draylar.gateofbabylon.registry.GOBEnchantments;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomBowItem extends BowItem implements EnchantmentHandler {

    private final Tier material;
    private final float maxDrawTime;
    private final ParticleOptions type;
    private final double damageModifier;

    public CustomBowItem(Tier material, Item.Properties settings, float maxDrawTime, double damageModifier) {
        super(settings);
        this.material = material;
        this.maxDrawTime = maxDrawTime;
        this.damageModifier = damageModifier;
        type = null;
    }

    public CustomBowItem(Tier material, Item.Properties settings, float maxDrawTime, double damageModifier, ParticleOptions particles) {
        super(settings);
        this.material = material;
        this.maxDrawTime = maxDrawTime;
        this.damageModifier = damageModifier;
        type = particles;
    }

    public float getMaxDrawTime(ItemStack bow) {
        int quickDrawLevel = EnchantmentHelper.getItemEnchantmentLevel(GOBEnchantments.QUICKDRAW, bow);
        return (float) Math.max(0, maxDrawTime - quickDrawLevel * 3.3);
    }

    public ParticleOptions getArrowParticles() {
        return type;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            boolean skipArrowCheck = playerEntity.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack arrowProjectile = playerEntity.getProjectile(stack);

            if (!arrowProjectile.isEmpty() || skipArrowCheck) {
                if (arrowProjectile.isEmpty()) {
                    arrowProjectile = new ItemStack(Items.ARROW);
                }

                int currentUseTime = this.getUseDuration(stack) - remainingUseTicks;
                float pullProgress = getPullProgress(stack, this, currentUseTime);

                if ((double) pullProgress >= 0.1D) {
                    boolean bl2 = skipArrowCheck && arrowProjectile.getItem() == Items.ARROW;

                    if (!world.isClientSide) {
                        ArrowItem arrowItem = (ArrowItem) (arrowProjectile.getItem() instanceof ArrowItem ? arrowProjectile.getItem() : Items.ARROW);
                        AbstractArrow arrowEntity = arrowItem.createArrow(world, arrowProjectile, playerEntity);
                        arrowEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, pullProgress * 3.0F, 1.0F);
                        ((ProjectileManipulator) arrowEntity).setOrigin(stack);

                        // Make Arrow crit if pull progress is fully complete
                        if (pullProgress == 1.0F) {
                            arrowEntity.setCritArrow(true);
                        }

                        // Apply damage from power enchantment
                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (j > 0) {
                            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) j * 0.5D + 0.5D);
                        }

                        // apply damage multiplier
                        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() * damageModifier);

                        // Apply punch knockback
                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (k > 0) {
                            arrowEntity.setKnockback(k);
                        }

                        // Apply flame
                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            arrowEntity.setSecondsOnFire(100);
                        }

                        // Damage tool
                        stack.hurtAndBreak(1, playerEntity, p -> p.broadcastBreakEvent(playerEntity.getUsedItemHand()));

                        // Set arrow pickup type based on source
                        if (bl2 || playerEntity.getAbilities().instabuild && (arrowProjectile.getItem() == Items.SPECTRAL_ARROW || arrowProjectile.getItem() == Items.TIPPED_ARROW)) {
                            arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        world.addFreshEntity(arrowEntity);
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

                    // decrement source arrow stack
                    if (!bl2 && !playerEntity.getAbilities().instabuild) {
                        arrowProjectile.shrink(1);
                        if (arrowProjectile.isEmpty()) {
                            playerEntity.getInventory().removeItem(arrowProjectile);
                        }
                    }

                    playerEntity.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public static float getPullProgress(ItemStack stack, CustomBowItem bow, int useTicks) {
        float progress = (float) useTicks / bow.getMaxDrawTime(stack);
        progress = (progress * progress + progress * 2.0F) / 3.0F;

        if (progress > 1.0F) {
            progress = 1.0F;
        }

        return progress;
    }

    @Override
    public int getEnchantmentValue() {
        return material.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.isValidRepairItem(stack, ingredient);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("gateofbabylon.bow_stats").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(" ").append(Component.translatable("gateofbabylon.bow_damage", damageModifier).withStyle(ChatFormatting.DARK_GREEN)));
        tooltip.add(Component.literal(" ").append(Component.translatable("gateofbabylon.bow_draw_speed", (double) maxDrawTime / 20).withStyle(ChatFormatting.DARK_GREEN)));
    }
}

