package draylar.gateofbabylon.impl.client;

import draylar.gateofbabylon.item.CustomBowItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BowPullPredicate implements ClampedItemPropertyFunction {

    private final CustomBowItem bow;

    public BowPullPredicate(CustomBowItem bow) {
        this.bow = bow;
    }

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (entity == null || entity.getUseItem() != stack) {
            return 0.0F;
        }
        return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / bow.getMaxDrawTime(stack);
    }
}
