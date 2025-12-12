package io.github.riverbytheocean.mods.serverkeys.util.network;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;

@Getter
@RequiredArgsConstructor
public class KeyAddData {

    private final ResourceLocation id;
    private final String name, category;
    private final int defKey;
    private final int[] modifiers;

}
