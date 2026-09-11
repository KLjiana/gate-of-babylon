package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GOBBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GateOfBabylon.MODID);

    private static <T extends Block> T register(String name, T block, Item.Properties properties) {
        BLOCKS.register(name, () -> block);
        GOBItems.ITEMS.register(name, () -> new BlockItem(block, properties));
        return block;
    }

    private static <T extends Block> T register(String name, T block) {
        BLOCKS.register(name, () -> block);
        return block;
    }

    public static void init() {
        // NO-OP; referencing this class initializes the deferred entries.
    }

    private GOBBlocks() {
    }
}
