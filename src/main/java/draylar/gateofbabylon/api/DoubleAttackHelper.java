package draylar.gateofbabylon.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.Timer;
import java.util.TimerTask;

public class DoubleAttackHelper {

    public static void queueDoubleAttack(ServerPlayer player, Entity target) {
        Timer timer = new Timer();

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(player.getServer() != null) {
                            player.getServer().execute(() -> {
                                if (target.isAlive()) {
                                    player.attack(target);
                                    player.swing(InteractionHand.MAIN_HAND);
                                }
                            });
                        }
                    }
                }, 250);
    }
}

