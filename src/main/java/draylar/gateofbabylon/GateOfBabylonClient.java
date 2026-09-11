package draylar.gateofbabylon;

import draylar.gateofbabylon.client.BoomerangEntityRenderer;
import draylar.gateofbabylon.client.SpearProjectileEntityRenderer;
import draylar.gateofbabylon.client.YoyoEntityRenderer;
import draylar.gateofbabylon.impl.client.BowPullPredicate;
import draylar.gateofbabylon.impl.client.BowPullingPredicate;
import draylar.gateofbabylon.impl.client.ShieldUsePredicate;
import draylar.gateofbabylon.item.CustomBowItem;
import draylar.gateofbabylon.registry.GOBEntities;
import draylar.gateofbabylon.registry.GOBItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Client-only registrations for renderers, item predicates, and additional models. */
@Mod.EventBusSubscriber(modid = GateOfBabylon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GateOfBabylonClient {

    public static final ModelResourceLocation DIAMOND_YOYO_MODEL = new ModelResourceLocation(GateOfBabylon.id("world_diamond_yoyo"), "inventory");
    public static final ModelResourceLocation NETHERITE_YOYO_MODEL = new ModelResourceLocation(GateOfBabylon.id("world_netherite_yoyo"), "inventory");
    public static final ModelResourceLocation GOLDEN_YOYO_MODEL = new ModelResourceLocation(GateOfBabylon.id("world_golden_yoyo"), "inventory");
    public static final ModelResourceLocation IRON_YOYO_MODEL = new ModelResourceLocation(GateOfBabylon.id("world_iron_yoyo"), "inventory");
    public static final ModelResourceLocation STONE_YOYO_MODEL = new ModelResourceLocation(GateOfBabylon.id("world_stone_yoyo"), "inventory");
    public static final ModelResourceLocation WOODEN_YOYO_MODEL = new ModelResourceLocation(GateOfBabylon.id("world_wooden_yoyo"), "inventory");

    private GateOfBabylonClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(GOBEntities.SPEAR.get(), context -> new SpearProjectileEntityRenderer(context, Minecraft.getInstance().getItemRenderer()));
            EntityRenderers.register(GOBEntities.YOYO.get(), YoyoEntityRenderer::new);
            EntityRenderers.register(GOBEntities.BOOMERANG.get(), BoomerangEntityRenderer::new);

            registerBowPredicates(GOBItems.STONE_BOW.get());
            registerBowPredicates(GOBItems.IRON_BOW.get());
            registerBowPredicates(GOBItems.GOLDEN_BOW.get());
            registerBowPredicates(GOBItems.DIAMOND_BOW.get());
            registerBowPredicates(GOBItems.NETHERITE_BOW.get());

            ResourceLocation blocking = new ResourceLocation(GateOfBabylon.MODID, "blocking");
            ItemProperties.register(GOBItems.STONE_SHIELD.get(), blocking, new ShieldUsePredicate());
            ItemProperties.register(GOBItems.IRON_SHIELD.get(), blocking, new ShieldUsePredicate());
            ItemProperties.register(GOBItems.GOLDEN_SHIELD.get(), blocking, new ShieldUsePredicate());
            ItemProperties.register(GOBItems.DIAMOND_SHIELD.get(), blocking, new ShieldUsePredicate());
            ItemProperties.register(GOBItems.NETHERITE_SHIELD.get(), blocking, new ShieldUsePredicate());
        });
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(DIAMOND_YOYO_MODEL);
        event.register(NETHERITE_YOYO_MODEL);
        event.register(GOLDEN_YOYO_MODEL);
        event.register(IRON_YOYO_MODEL);
        event.register(STONE_YOYO_MODEL);
        event.register(WOODEN_YOYO_MODEL);
    }

    private static void registerBowPredicates(CustomBowItem bow) {
        ItemProperties.register(bow, new ResourceLocation(GateOfBabylon.MODID, "pull"), new BowPullPredicate(bow));
        ItemProperties.register(bow, new ResourceLocation(GateOfBabylon.MODID, "pulling"), new BowPullingPredicate());
    }
}
