package io.github.riverbytheocean.mods.serverkeys;

import io.github.riverbytheocean.mods.serverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.serverkeys.payloads.AddKeyPayload;
import io.github.riverbytheocean.mods.serverkeys.payloads.HandshakePayload;
import io.github.riverbytheocean.mods.serverkeys.payloads.KeyPressPayload;
import io.github.riverbytheocean.mods.serverkeys.payloads.LoadKeysPayload;
import io.github.riverbytheocean.mods.serverkeys.util.network.KeyAddData;
import io.github.riverbytheocean.mods.serverkeys.util.saving.KeyMappingSaving;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceLocation;

public class ServerKeysClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        PayloadTypeRegistry.playS2C().register(AddKeyPayload.TYPE, AddKeyPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HandshakePayload.TYPE, HandshakePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(KeyPressPayload.TYPE, KeyPressPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LoadKeysPayload.TYPE, LoadKeysPayload.CODEC);

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> ServerKeys.handleDisconnect()));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerKeys.handleConnect());

        ClientPlayNetworking.registerGlobalReceiver(AddKeyPayload.TYPE, ((payload, context) -> {

            KeyAddData data = new KeyAddData(
                    ResourceLocation.fromNamespaceAndPath(payload.namespace(), payload.key()),
                    payload.name(),
                    payload.category(),
                    payload.defKey(),
                    new int[]{-1}
            );

            context.client().execute(() -> ServerKeys.add(data));
        }));

        ClientPlayNetworking.registerGlobalReceiver(LoadKeysPayload.TYPE, ((payload, context) -> context.client().execute(KeyMappingSaving::load)));

    }

    public static ResourceLocation from(String value) {
        return ResourceLocation.fromNamespaceAndPath("serverkeys", value);
    }

}
