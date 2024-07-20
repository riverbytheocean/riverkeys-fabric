package io.github.riverbytheocean.mods.riverkeys.util.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;

@Getter
@RequiredArgsConstructor
public class KeyPressData {

    private final ResourceLocation id;
    private final boolean release;

}
