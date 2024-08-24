package io.github.riverbytheocean.mods.riverkeys.keymappings;

import io.github.riverbytheocean.mods.riverkeys.util.KeyCategoryComparator;
import io.github.riverbytheocean.mods.riverkeys.util.KeyModifierComparator;
import io.github.riverbytheocean.mods.riverkeys.util.network.KeyAddData;
import io.github.riverbytheocean.mods.riverkeys.util.saving.KeyMappingSaving;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public final class ServerKeys {

    private static final Map<ResourceLocation, ServerKey> CUSTOM_KEYS = new HashMap<>();
    private static final Set<ResourceLocation> VANILLA_KEYS = new HashSet<>();

    private static final Comparator<ServerKey> CATEGORY_COMPARATOR = new KeyCategoryComparator(),
            MODIFIER_COMPARATOR = new KeyModifierComparator().reversed();

    public static Collection<ServerKey> getKeybinds() {
        return CUSTOM_KEYS.values();
    }

    public static void handleConnect() {
        // Clean up, then perform handshake protocol
        ServerKeys.clear();
        KeyPacketSending.sendHandshake();
    }

    public static void handleDisconnect() {
        // Clean up after disconnection
        KeyMappingSaving.save();
        ServerKeys.clear();
    }

    /* Custom sorting rules as running Collections.sort()
     will cause a crash, since these keybinds aren't
     registered the usual way. */
    public static List<ServerKey> getCategorySortedKeybinds() {
        List<ServerKey> set = new ArrayList<>(CUSTOM_KEYS.values());
        set.sort(CATEGORY_COMPARATOR);
        return set;
    }

    public static List<ServerKey> getModifierSortedKeybinds() {
        List<ServerKey> set = new ArrayList<>(CUSTOM_KEYS.values());
        set.sort(MODIFIER_COMPARATOR);
        return set;
    }

    public static Set<ResourceLocation> getVanillaKeys() {
        return VANILLA_KEYS;
    }

    public static ResourceLocation cleanIdentifier(String key) {
        return ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, key.replace("key.", "").replace(".", "").toLowerCase());
    }

    public static void clear() {
        VANILLA_KEYS.clear();
        CUSTOM_KEYS.clear();
    }

    public static void add(KeyAddData key) {
        ResourceLocation id = key.getId();
        if (id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) VANILLA_KEYS.add(id);
        else CUSTOM_KEYS.put(id, new ServerKey(key));
    }

}
