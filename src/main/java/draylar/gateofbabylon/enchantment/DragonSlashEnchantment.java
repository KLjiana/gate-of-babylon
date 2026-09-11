package draylar.gateofbabylon.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

public class DragonSlashEnchantment extends KatanaSlashEnchantment {

    public DragonSlashEnchantment() {
        super(SoundEvents.ENDER_DRAGON_AMBIENT, ParticleTypes.WITCH);
    }
}
