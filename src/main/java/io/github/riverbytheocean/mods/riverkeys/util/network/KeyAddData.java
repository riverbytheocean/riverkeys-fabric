package io.github.riverbytheocean.mods.riverkeys.util.network;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Getter
@RequiredArgsConstructor
public class KeyAddData {

    private final ResourceLocation id;
    private final String name, category;
    private final int defKey;
    private final int[] modifiers;

    public static KeyAddData fromBuffer(FriendlyByteBuf buf) {
        String path = buf.readUtf();
        String key = buf.readUtf();
        int defKey = buf.readInt();
        String name = buf.readUtf();
        String category = buf.readUtf();
        int[] modifiers = buf.readVarIntArray();

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(path, key);
        return new KeyAddData(id, name, category, defKey, modifiers);
    }

}
