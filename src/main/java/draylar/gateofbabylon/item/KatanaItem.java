package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.enchantment.DragonSlashEnchantment;
import draylar.gateofbabylon.enchantment.KatanaSlashEnchantment;
import draylar.gateofbabylon.entity.DragonSlashBreathEntity;
import draylar.gateofbabylon.registry.GOBEffects;
import draylar.gateofbabylon.registry.GOBSounds;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KatanaItem extends SwordItem implements EnchantmentHandler {

    private final float attackDamage;

    public KatanaItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamageBonus() - 1), -4 + effectiveSpeed, settings);
        attackDamage = effectiveDamage;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1 || user.isCrouching()) {
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
            KatanaSlashEnchantment enchantment = getSlashEnchantment(stack);

            if (currentUseTime >= 10) {
                if (!world.isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) world;

                    stack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(user.getUsedItemHand()));
                    HitResult rayTrace = raycast(user, 16, 0, false);

                    // Play SFX
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), GOBSounds.KATANA_SWOOP, SoundSource.PLAYERS, 0.5F, 1.0F);
                    if(enchantment != null) {
                        world.playSound(null, user.getX(), user.getY(), user.getZ(), enchantment.getSound(), SoundSource.PLAYERS, .5F, 0.25F);
                    }

                    // calculate line from player to target
                    Vec3 distanceVec = rayTrace.getLocation().subtract(user.position());
                    double distance = Math.sqrt(Math.pow(distanceVec.x, 2) + Math.pow(distanceVec.y, 2) + Math.pow(distanceVec.z, 2)); // distance from player to target
                    Vec3 addPerBlock = distanceVec.scale(1 / distance);
                    Vec3 currentPos = user.position().add(0, 0, 0);

                    // store hit entities
                    List<UUID> hitEntities = new ArrayList<>();

                    // iterate over each block between player and target
                    for(int i = 0; i  < distance; i++) {
                        serverWorld.sendParticles(ParticleTypes.CRIT, currentPos.x, currentPos.y + .5, currentPos.z, 5, 0, 0, 0, .1);
                        currentPos = currentPos.add(addPerBlock);

                        if(enchantment != null) {
                            // Create Dragon Breath clouds along path if enchantment is present
                            if (enchantment instanceof DragonSlashEnchantment) {
                                DragonSlashBreathEntity cloud = new DragonSlashBreathEntity(world, currentPos.x, currentPos.y + .2, currentPos.z);
                                cloud.setOwner(user);
                                cloud.setRadius(0.75F);
                                cloud.setDuration(60);
                                cloud.setParticle(ParticleTypes.DRAGON_BREATH);
                                cloud.addEffect(new MobEffectInstance(GOBEffects.DRAGON_SLASH_EFFECT));
                                world.addFreshEntity(cloud);
                            } else {
                                serverWorld.sendParticles(enchantment.getParticle(), currentPos.x, currentPos.y + .5, currentPos.z, 5, 0, 0, 0, .1);
                            }
                        }

                        // check for small box around the current position for enemies
                        world.getEntitiesOfClass(LivingEntity.class, new AABB(currentPos.add(-2, -2, -2), currentPos.add(2, 2, 2)), entity -> !hitEntities.contains(entity.getUUID()) && entity != user).forEach(entity -> {
                            // Triggers for entities that aren't tameable, or that aren't tamed, or that aren't owned by the owner of the breath
                            if (!(entity instanceof TamableAnimal) || !((TamableAnimal) entity).isTame() || !((TamableAnimal) entity).getOwnerUUID().equals(player.getUUID())) {
                                // Apply enchantment effects
                                if(enchantment != null) {
                                    enchantment.onHit(entity, player, stack);
                                }

                                // Damage entity with stack's power
                                entity.hurt(entity.damageSources().playerAttack((Player) user), 1.25f * (EnchantmentHelper.getDamageBonus(stack, entity.getMobType()) + attackDamage));

                                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), GOBSounds.KATANA_SWOOP, SoundSource.PLAYERS, 2F, 1.5F + (float) world.random.nextDouble() * .5f);
                                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5F, 1.5F + (float) world.random.nextDouble() * .5f);
                                serverWorld.sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + .5, entity.getZ(), 25, 0, 0, 0, .1);
                                hitEntities.add(entity.getUUID());
                            }
                        });
                    }

                    // Teleport forwards
                    user.setPos(rayTrace.getLocation().x - distanceVec.normalize().x, rayTrace.getLocation().y + .5, rayTrace.getLocation().z - distanceVec.normalize().z);
                }

                player.getCooldowns().addCooldown(this, 20 * 10); // 10 second cd
                    player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public KatanaSlashEnchantment getSlashEnchantment(ItemStack stack) {
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment instanceof KatanaSlashEnchantment) {
                return (KatanaSlashEnchantment) enchantment;
            }
        }

        return null;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public HitResult raycast(Entity from, double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3 cameraPosVec = from.getEyePosition(tickDelta);
        Vec3 rotationVec = from.getViewVector(tickDelta);
        Vec3 vec3d3 = cameraPosVec.add(rotationVec.x * maxDistance, 0 * maxDistance - 1.5, rotationVec.z * maxDistance);
        return from.level().clip(new ClipContext(cameraPosVec, vec3d3, ClipContext.Block.OUTLINE, includeFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, from));
    }
}

