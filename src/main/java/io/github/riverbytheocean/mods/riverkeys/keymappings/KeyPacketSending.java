package io.github.riverbytheocean.mods.riverkeys.keymappings;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.Riverkeys;
import io.github.riverbytheocean.mods.riverkeys.mixin.client.KeyMappingAccessor;
import io.github.riverbytheocean.mods.riverkeys.payloads.HandshakePayload;
import io.github.riverbytheocean.mods.riverkeys.payloads.KeyPressPayload;
import io.github.riverbytheocean.mods.riverkeys.util.network.KeyPressData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;

public class KeyPacketSending {

    public static void sendHandshake() {
        ClientPlayNetworking.send(new HandshakePayload());
        Riverkeys.LOGGER.info("handshake sent!");
    }

    public static KeyMapping getKeyBinding(InputConstants.Key code) {
        return KeyMappingAccessor.getKeyBindings().get(code);
    }

    public static void sendKey(KeyPressData data) {
        ClientPlayNetworking.send(new KeyPressPayload(data.getId().getNamespace(), data.getId().getPath(), data.isRelease()));
    }

}
