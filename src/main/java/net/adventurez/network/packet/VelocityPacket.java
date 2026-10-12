package net.adventurez.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record VelocityPacket(int entityId, float velocity) implements CustomPacketPayload {

    public static final CustomPacketPayload.Id<VelocityPacket> PACKET_ID = new CustomPacketPayload.Id<>(Identifier.of("adventurez", "velocity_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VelocityPacket> PACKET_CODEC = StreamCodec.of((value, buf) -> {
        buf.writeInt(value.entityId);
        buf.writeFloat(value.velocity);
    }, buf -> new VelocityPacket(buf.readInt(), buf.readFloat()));

    @Override
    public Id<? extends CustomPacketPayload> getId() {
        return PACKET_ID;
    }

}
