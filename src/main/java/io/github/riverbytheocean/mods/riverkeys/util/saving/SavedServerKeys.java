package io.github.riverbytheocean.mods.riverkeys.util.saving;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class SavedServerKeys {

    // string represents the namespace
    Set<SavedServerKey> savedServerKeys = new HashSet<>();

    public SavedServerKey getSavedServerKey(String namespace, String id) {
        for (SavedServerKey serverKey : savedServerKeys) {
            if (Objects.equals(serverKey.namespace, namespace) && Objects.equals(serverKey.id, id)) {
                return serverKey;
            }
        }
        return null;
    }

    public void changeProperties(String namespace, String id, String keyName, List<Integer> modifiers) {

        if (getSavedServerKey(namespace, id) == null) {
            savedServerKeys.add(new SavedServerKey(namespace, id, keyName, modifiers));
            return;
        }

        getSavedServerKey(namespace, id).keyName = keyName;
        getSavedServerKey(namespace, id).modifiers = modifiers;

    }

}
