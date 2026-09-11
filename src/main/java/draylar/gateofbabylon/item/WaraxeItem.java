package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.registry.GOBEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.UseAnim;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class WaraxeItem extends AxeItem implements EnchantmentHandler {

    private final float attackDamage;

    public WaraxeItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamageBonus() - 1), -4 + effectiveSpeed, settings);
        this.attackDamage = effectiveDamage;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        boolean hasSmashing = EnchantmentHelper.getItemEnchantmentLevel(GOBEnchantments.SMASHING, stack) > 0;
        int radius = hasSmashing ? 5 : 3;

        if (!world.isClientSide && user instanceof Player) {
            Player player = (Player) user;

            // spawn effects
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

                    if (distance <= radius && distance >= radius / 2f) {
                        Vec3 newPos = user.position().add(x, -2, z);
                        int level = 0;

                        while(!world.getBlockState(BlockPos.containing(newPos).above()).isAir() && level < 5) {
                            newPos = newPos.add(0, 1, 0);
                            level++;
                        }

                        if(world.getBlockState(BlockPos.containing(newPos).above()).isAir()) {
                            addFreshEntity((ServerLevel) world, newPos.add(0, 1, 0), user, world.getBlockState(BlockPos.containing(newPos)));
                        }
                    }
                }
            }

            // knock back nearby entities
            world.getEntitiesOfClass(LivingEntity.class, new AABB(user.blockPosition().offset(-radius - 2, -1, -radius - 2), user.blockPosition().offset(radius + 2, 3, radius + 2)), entity -> entity != user).forEach(entity -> {
                // Triggers for entities that aren't tameable, or that aren't tamed, or that aren't owned by the owner of the breath
                if (!(entity instanceof TamableAnimal) || !((TamableAnimal) entity).isTame() || !((TamableAnimal) entity).getOwnerUUID().equals(player.getUUID())) {
                    entity.hurt(entity.level().damageSources().playerAttack(player), hasSmashing ? attackDamage * 1.5f : attackDamage);
                    entity.setDeltaMovement(entity.position().subtract(player.position()).scale(hasSmashing ? .6 : .5).add(0, .35, 0));
                }
            });

            player.getCooldowns().addCooldown(this, 20 * 5); // 20 * 5
        }

        return super.finishUsingItem(stack, world, user);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    public void addFreshEntity(ServerLevel world, Vec3 pos, LivingEntity source, BlockState state) {
        FallingBlockEntity spawn = FallingBlockEntity.fall(world, BlockPos.containing(pos), state);

        // setup velocity
        Vec3 difference = pos.subtract(source.position()).scale(.1);
        spawn.setDeltaMovement(spawn.getDeltaMovement().add(0.0D, 0.35D, 0.0D)
                .add(source.getDeltaMovement()).add(difference));

        // spawn particles
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.x, pos.y, pos.z, 3, 0, 0, 0, .1);
        world.playSound(null, pos.x, pos.y, pos.z, state.getSoundType().getPlaceSound(), SoundSource.PLAYERS, .25f, .5f + world.random.nextInt() * .25f);

        // setup properties
        spawn.dropItem = false;
        spawn.disableDrop();
        spawn.time = 5;

        // spawn
        world.addFreshEntity(spawn);
    }
}

