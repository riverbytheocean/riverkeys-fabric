package io.github.riverbytheocean.mods.riverkeys.util;

import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;

import java.util.Comparator;

public class KeyCategoryComparator implements Comparator<ServerKey> {
    public int compare(ServerKey key1, ServerKey key2) {
        int id = key1.getId().compareTo(key2.getId());
        int category = key1.getCategory().compareTo(key2.getCategory());
        return Integer.compare(category, id);
    }
}
