package draylar.gateofbabylon.enchantment;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ThunderSlashEnchantment extends KatanaSlashEnchantment {
    
    public ThunderSlashEnchantment() {
        super(SoundEvents.LIGHTNING_BOLT_THUNDER, ParticleTypes.CLOUD, (target, source, stack) -> {
            if (target.level() instanceof ServerLevel serverLevel) {
                BlockPos blockPos = target.blockPosition();
                if (serverLevel.getBrightness(net.minecraft.world.level.LightLayer.SKY, blockPos) > 0) {
                    @Nullable LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    if(lightning != null) {
                        lightning.moveTo(Vec3.atBottomCenterOf(blockPos));
                        lightning.setCause(source instanceof ServerPlayer ? (ServerPlayer) source : null);
                        serverLevel.addFreshEntity(lightning);
                    }
                }
            }
        });
    }
}

