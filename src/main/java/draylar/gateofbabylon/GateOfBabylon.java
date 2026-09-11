package draylar.gateofbabylon;

import draylar.gateofbabylon.impl.BoomerangDispenserBehavior;
import draylar.gateofbabylon.registry.GOBBlocks;
import draylar.gateofbabylon.registry.GOBEffects;
import draylar.gateofbabylon.registry.GOBEnchantments;
import draylar.gateofbabylon.registry.GOBEntities;
import draylar.gateofbabylon.registry.GOBItems;
import draylar.gateofbabylon.registry.GOBSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(GateOfBabylon.MODID)
public class GateOfBabylon {

    public static final String MODID = "gateofbabylon";

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> GROUP = CREATIVE_MODE_TABS.register("group", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gateofbabylon.group"))
                    .icon(() -> new ItemStack(GOBItems.DIAMOND_SPEAR.get()))
                    .displayItems((parameters, output) -> GOBItems.CREATIVE_TAB_ITEMS.forEach(item -> output.accept(item.get())))
                    .build());

    public GateOfBabylon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        GOBItems.ITEMS.register(modEventBus);
        GOBBlocks.BLOCKS.register(modEventBus);
        GOBEffects.MOB_EFFECTS.register(modEventBus);
        GOBEnchantments.ENCHANTMENTS.register(modEventBus);
        GOBEntities.ENTITY_TYPES.register(modEventBus);
        GOBSounds.SOUND_EVENTS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
                DispenserBlock.registerBehavior(GOBItems.DIAMOND_BOOMERANG.get(), new BoomerangDispenserBehavior()));
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
