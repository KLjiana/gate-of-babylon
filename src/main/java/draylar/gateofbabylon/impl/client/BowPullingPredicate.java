package draylar.gateofbabylon.impl.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BowPullingPredicate implements ClampedItemPropertyFunction {

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
    }
}
