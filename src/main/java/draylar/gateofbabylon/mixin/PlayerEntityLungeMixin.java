package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.LungeManipulator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityLungeMixin extends LivingEntity implements LungeManipulator {

    @Unique private boolean gob$hasLunged = false;

    protected PlayerEntityLungeMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void gateOfBabylon$onPlayerTick(CallbackInfo ci) {
        if (gob$hasLunged && onGround()) {
            gob$hasLunged = false;
        }
    }

    @Unique
    @Override
    public void gateOfBabylon$setLunged() {
        gob$hasLunged = true;
    }

    @Unique
    @Override
    public boolean gateOfBabylon$canLunge() {
        return !gob$hasLunged;
    }
}

