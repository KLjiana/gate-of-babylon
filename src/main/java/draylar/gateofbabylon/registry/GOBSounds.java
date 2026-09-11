package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GOBSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GateOfBabylon.MODID);

    public static final SoundEvent KATANA_SWOOP = register("katana_swoop", SoundEvent.createVariableRangeEvent(new ResourceLocation(GateOfBabylon.MODID, "katana_swoop")));

    private static SoundEvent register(String name, SoundEvent sound) {
        SOUND_EVENTS.register(name, () -> sound);
        return sound;
    }

    public static void init() {
        // NO-OP; referencing this class initializes the deferred entries.
    }

    private GOBSounds() {
    }
}
