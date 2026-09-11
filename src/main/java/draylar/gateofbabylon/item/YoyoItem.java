package draylar.gateofbabylon.item;

import draylar.gateofbabylon.entity.YoyoEntity;
import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class YoyoItem extends TieredItem {

    private final Tier material;

    public YoyoItem(Item.Properties settings, Tier material) {
        super(material, settings);
        this.material = material;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if(!world.isClientSide) {
            // If the user already has a yoyo out, we want to retract it.
            // Otherwise, we spawn a new yoyo and send it flying out / away from the player.
            List<YoyoEntity> found = new ArrayList<>(world.getEntitiesOfClass(
                    YoyoEntity.class,
                    new AABB(user.blockPosition().offset(-25, -25, -25), user.blockPosition().offset(25, 25, 25)),
                    yoyo -> yoyo.isAlive() && yoyo.getOwner().isPresent() && yoyo.getOwner().get().equals(user.getUUID())));

            // Yoyo was found, remove it and stop early.
            if(!found.isEmpty()) {
                found.forEach(yoyo -> {
                    yoyo.retract();

                    // TOOD: retract instead of removing instantly?
                    yoyo.remove(Entity.RemovalReason.DISCARDED);
                    yoyo.kill();
                });

                return InteractionResultHolder.success(user.getItemInHand(hand));
            }

            // Yoyo was not found, spawn a new one now.
            YoyoEntity yoyo = new YoyoEntity(GOBEntities.YOYO.get(), world);
            yoyo.setPos(user.getX(), user.getY(), user.getZ());
            yoyo.setPos(user.getX(), user.getY(), user.getZ());
            yoyo.setPos(user.getX(), user.getY(), user.getZ());
            yoyo.setOwner(user);
            yoyo.setStack(user.getItemInHand(hand));
            world.addFreshEntity(yoyo);
            yoyo.deploy();
        }

        user.startUsingItem(hand);
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        super.releaseUsing(stack, world, user, remainingUseTicks);

        // Stop using yoyo
        List<YoyoEntity> found = new ArrayList<>(world.getEntitiesOfClass(
                YoyoEntity.class,
                new AABB(user.blockPosition().offset(-25, -25, -25), user.blockPosition().offset(25, 25, 25)),
                yoyo -> yoyo.isAlive() && yoyo.getOwner().isPresent() && yoyo.getOwner().get().equals(user.getUUID())));

        // Yoyo was found, remove it and stop early.
        if(!found.isEmpty()) {
            found.forEach(yoyo -> {
                yoyo.retract();

                // TOOD: retract instead of removing instantly?
                yoyo.remove(Entity.RemovalReason.DISCARDED);
                yoyo.kill();
            });
        }
    }

    public Tier getMaterial() {
        return material;
    }
}

