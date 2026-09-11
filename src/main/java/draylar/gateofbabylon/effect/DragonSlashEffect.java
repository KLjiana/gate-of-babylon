package draylar.gateofbabylon.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DragonSlashEffect extends InstantenousMobEffect {

    public DragonSlashEffect() {
        super(MobEffectCategory.HARMFUL, 0xcc00d9);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().magic(), 4 << amplifier);
    }
}

