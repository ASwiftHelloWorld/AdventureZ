package net.adventurez.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;

public class EventInit {

    public static void init() {
        ServerPlayerEvents.AFTER_RESPAWN.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) -> {
            if (alive && oldPlayer.hasStatusEffect(EffectInit.FAME)) {
                newPlayer.addStatusEffect(new MobEffectInstance(EffectInit.FAME, oldPlayer.getStatusEffect(EffectInit.FAME).getDuration(), 0, false, false, true));
            }
        });
    }

}
