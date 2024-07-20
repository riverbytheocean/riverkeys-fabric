package io.github.riverbytheocean.mods.riverkeys.util;

import net.minecraft.resources.ResourceLocation;

public class KeyPacketChannels {

    // backwards compatibility with AriKeys
    public static final ResourceLocation HANDSHAKE_CHANNEL = ResourceLocation.fromNamespaceAndPath("arikeys", "greeting");
    public static final ResourceLocation ADD_KEY_CHANNEL = ResourceLocation.fromNamespaceAndPath("arikeys", "addkey");
    public static final ResourceLocation LOAD_CHANNEL = ResourceLocation.fromNamespaceAndPath("arikeys", "load");
    public static final ResourceLocation KEY_CHANNEL = ResourceLocation.fromNamespaceAndPath("arikeys", "keybind");

}
