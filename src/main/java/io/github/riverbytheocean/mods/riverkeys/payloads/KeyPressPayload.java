package io.github.riverbytheocean.mods.riverkeys.payloads;

import io.github.riverbytheocean.mods.riverkeys.util.KeyPacketChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record KeyPressPayload(String namespace, String key, boolean release) implements CustomPacketPayload {

    public static final Type<KeyPressPayload> TYPE = new Type<>(KeyPacketChannels.KEY_CHANNEL);

    public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, KeyPressPayload::namespace,
            ByteBufCodecs.STRING_UTF8, KeyPressPayload::key,
            ByteBufCodecs.BOOL, KeyPressPayload::release,
            KeyPressPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
