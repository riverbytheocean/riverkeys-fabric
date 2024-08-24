package io.github.riverbytheocean.mods.riverkeys.util.saving;

import java.util.List;

class SavedServerKey {

    String namespace;
    String id;
    String keyName;
    List<Integer> modifiers;

    SavedServerKey(String namespace, String id, String keyName, List<Integer> modifiers) {

        this.namespace = namespace;
        this.id = id;
        this.keyName = keyName;
        this.modifiers = modifiers;

    }

}
