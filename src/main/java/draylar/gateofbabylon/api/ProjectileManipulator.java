package draylar.gateofbabylon.api;

import net.minecraft.world.item.ItemStack;

public interface ProjectileManipulator {
    void setOrigin(ItemStack stack);
    ItemStack getOrigin();
}

