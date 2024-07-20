package io.github.riverbytheocean.mods.riverkeys.payloads;

import io.github.riverbytheocean.mods.riverkeys.util.KeyPacketChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record LoadKeysPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LoadKeysPayload> TYPE = new CustomPacketPayload.Type<>(KeyPacketChannels.LOAD_CHANNEL);

    public static final StreamCodec<RegistryFriendlyByteBuf, LoadKeysPayload> CODEC = StreamCodec.unit(new LoadKeysPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
