package io.github.riverbytheocean.mods.riverkeys.util;

import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;

import java.util.Comparator;

public class KeyModifierComparator implements Comparator<ServerKey> {

    public int compare(ServerKey key1, ServerKey key2) {
        return Integer.compare(key1.getBoundModifiers().size(), key2.getBoundModifiers().size());
    }

}
