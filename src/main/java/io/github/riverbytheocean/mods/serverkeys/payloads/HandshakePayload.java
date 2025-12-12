package io.github.riverbytheocean.mods.serverkeys.payloads;

import io.github.riverbytheocean.mods.serverkeys.util.KeyPacketChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HandshakePayload() implements CustomPacketPayload {

    public static final Type<HandshakePayload> TYPE = new Type<>(KeyPacketChannels.HANDSHAKE_CHANNEL);

    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakePayload> CODEC = StreamCodec.unit(new HandshakePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
