package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.entity.YoyoEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class GOBEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GateOfBabylon.MODID);

    public static final RegistryObject<EntityType<SpearProjectileEntity>> SPEAR = ENTITY_TYPES.register(
            "spear",
            () -> EntityType.Builder.<SpearProjectileEntity>of((type, level) -> new SpearProjectileEntity(type, level), MobCategory.MISC)
                    .sized(.5f, .5f)
                    .setTrackingRange(128)
                    .setUpdateInterval(4)
                    .build("spear"));

    public static final RegistryObject<EntityType<YoyoEntity>> YOYO = ENTITY_TYPES.register(
            "yoyo",
            () -> EntityType.Builder.<YoyoEntity>of((type, level) -> new YoyoEntity(type, level), MobCategory.MISC)
                    .sized(.25f, .25f)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("yoyo"));

    public static final RegistryObject<EntityType<BoomerangEntity>> BOOMERANG = ENTITY_TYPES.register(
            "boomerang",
            () -> EntityType.Builder.<BoomerangEntity>of((type, level) -> new BoomerangEntity(type, level), MobCategory.MISC)
                    .sized(.5f, .1f)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("boomerang"));

    public static void init() {
        // NO-OP; referencing this class initializes the deferred entries.
    }

    private GOBEntities() {
    }
}
