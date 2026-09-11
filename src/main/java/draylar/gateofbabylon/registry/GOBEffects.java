package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.effect.DragonSlashEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GOBEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GateOfBabylon.MODID);

    public static final DragonSlashEffect DRAGON_SLASH_EFFECT = register("dragon_slash", new DragonSlashEffect());

    private static <T extends MobEffect> T register(String name, T effect) {
        MOB_EFFECTS.register(name, () -> effect);
        return effect;
    }

    public static void init() {
        // NO-OP; referencing this class initializes the deferred entries.
    }

    private GOBEffects() {
    }
}
