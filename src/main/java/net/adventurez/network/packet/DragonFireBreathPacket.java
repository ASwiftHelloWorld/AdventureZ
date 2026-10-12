package net.adventurez.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DragonFireBreathPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Id<DragonFireBreathPacket> PACKET_ID = new CustomPacketPayload.Id<>(Identifier.of("adventurez", "dragon_fire_breath_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DragonFireBreathPacket> PACKET_CODEC = StreamCodec.of((value, buf) -> {
    }, buf -> new DragonFireBreathPacket());

    @Override
    public Id<? extends CustomPacketPayload> getId() {
        return PACKET_ID;
    }

}
