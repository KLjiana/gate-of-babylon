package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.enchantment.DragonSlashEnchantment;
import draylar.gateofbabylon.enchantment.KatanaSlashEnchantment;
import draylar.gateofbabylon.enchantment.LungingEnchantment;
import draylar.gateofbabylon.enchantment.QuickDrawEnchantment;
import draylar.gateofbabylon.enchantment.SmashingEnchantment;
import draylar.gateofbabylon.enchantment.ThunderSlashEnchantment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class GOBEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, GateOfBabylon.MODID);

    public static final LungingEnchantment LUNGING = register("lunging", new LungingEnchantment());
    public static final SmashingEnchantment SMASHING = register("smashing", new SmashingEnchantment());
    public static final DragonSlashEnchantment DRAGON_SLASH = register("dragon_slash", new DragonSlashEnchantment());
    public static final ThunderSlashEnchantment THUNDER_SLASH = register("thunder_slash", new ThunderSlashEnchantment());
    public static final KatanaSlashEnchantment FLAME_SLASH = register("flame_slash", new KatanaSlashEnchantment(SoundEvents.FIRE_AMBIENT, ParticleTypes.FLAME, (target, source, stack) -> target.setSecondsOnFire(6)));
    public static final KatanaSlashEnchantment VAMPIRE_SLASH = register("vampire_slash", new KatanaSlashEnchantment(SoundEvents.MAGMA_CUBE_SQUISH, ParticleTypes.HEART, (target, source, stack) -> source.heal(2.0f)));
    public static final QuickDrawEnchantment QUICKDRAW = register("quickdraw", new QuickDrawEnchantment());

    private static <T extends Enchantment> T register(String name, T enchantment) {
        ENCHANTMENTS.register(name, () -> enchantment);
        return enchantment;
    }

    public static void init() {
        // NO-OP; referencing this class initializes the deferred entries.
    }

    private GOBEnchantments() {
    }
}
