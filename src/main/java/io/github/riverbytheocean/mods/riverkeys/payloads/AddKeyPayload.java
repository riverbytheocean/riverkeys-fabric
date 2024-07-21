package io.github.riverbytheocean.mods.riverkeys.payloads;

import io.github.riverbytheocean.mods.riverkeys.util.KeyPacketChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record AddKeyPayload(String namespace, String key, int defKey, String name, String category) implements CustomPacketPayload {

    public static final Type<AddKeyPayload> TYPE = new Type<>(KeyPacketChannels.ADD_KEY_CHANNEL);

    public static final StreamCodec<RegistryFriendlyByteBuf, AddKeyPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AddKeyPayload::namespace,
            ByteBufCodecs.STRING_UTF8, AddKeyPayload::key,
            ByteBufCodecs.INT, AddKeyPayload::defKey,
            ByteBufCodecs.STRING_UTF8, AddKeyPayload::name,
            ByteBufCodecs.STRING_UTF8, AddKeyPayload::category,
            AddKeyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
