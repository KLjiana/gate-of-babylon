package draylar.gateofbabylon.entity;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.Level;

/** Area-effect cloud used by Dragon Slash; the vanilla cloud handles ticking and effects. */
public class DragonSlashBreathEntity extends AreaEffectCloud {

    public DragonSlashBreathEntity(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
