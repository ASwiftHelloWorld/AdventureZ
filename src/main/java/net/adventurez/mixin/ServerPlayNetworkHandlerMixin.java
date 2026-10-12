package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.adventurez.entity.DragonEntity;
import net.adventurez.entity.EnderWhaleEntity;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    private int vehicleFloatingTicks;

    @Inject(method = "onClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;updateLastActionTime()V"))
    private void onClientCommandMixin(ServerboundPlayerCommandPacket packet, CallbackInfo info) {
        if (player.getVehicle() != null && player.getVehicle() instanceof DragonEntity && ((DragonEntity) this.player.getVehicle()).hasChest()) {
            ((DragonEntity) this.player.getVehicle()).openInventory(this.player);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getControllingPassenger()Lnet/minecraft/entity/LivingEntity;", shift = Shift.AFTER, ordinal = 1))
    private void tickMixin(CallbackInfo info) {
        if (vehicleFloatingTicks >= 70 && player.getVehicle() != null && (player.getVehicle() instanceof DragonEntity || player.getVehicle() instanceof EnderWhaleEntity)) {
            vehicleFloatingTicks = 0;
        }
    }

}