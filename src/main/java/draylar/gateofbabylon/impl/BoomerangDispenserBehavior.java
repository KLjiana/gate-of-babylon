package draylar.gateofbabylon.impl;

import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.item.BoomerangItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class BoomerangDispenserBehavior implements DispenseItemBehavior {

    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos facing = source.getPos().relative(direction);

        if (stack.getItem() instanceof BoomerangItem boomerangItem) {
            BoomerangEntity boomerang = boomerangItem.createBoomerang(stack, source.getLevel());
            boomerang.setPos(facing.getX() + 0.5D, facing.getY() + 0.5D, facing.getZ() + 0.5D);
            boomerang.setDeltaMovement(direction.getStepX(), direction.getStepY() + 0.1D, direction.getStepZ());
            boomerang.setTemporary();
            source.getLevel().addFreshEntity(boomerang);
        }

        return stack;
    }
}
