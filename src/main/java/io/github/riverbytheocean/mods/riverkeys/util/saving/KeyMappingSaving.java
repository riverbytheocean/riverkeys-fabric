package io.github.riverbytheocean.mods.riverkeys.util.saving;

import com.google.common.base.MoreObjects;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class KeyMappingSaving {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final File KEYBIND_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "riverkeys.toml");

    public static void load() {

        if (!KEYBIND_FILE.exists()) return;

        SavedServerKeys serverKeys = MoreObjects.firstNonNull(new Toml().read(KEYBIND_FILE).to(SavedServerKeys.class), new SavedServerKeys());

        ServerKeys.getKeybinds().forEach(serverKey -> {
            String namespace = serverKey.getId().getNamespace();
            String id = serverKey.getId().getPath();

            String defKey = serverKey.getBoundKeyCode().getName();
            String keybind = MoreObjects.firstNonNull(serverKeys.getSavedServerKey(namespace, id).keyName, defKey);

            Set<ModifierKey> modifiers = ModifierKey.getFromList(serverKeys.getSavedServerKey(namespace, id).modifiers);

            if (!defKey.equals(keybind) || !modifiers.containsAll(serverKey.getModifiers())) {
                serverKey.setBoundKey(InputConstants.getKey(keybind), false);
                serverKey.setBoundModifiers(modifiers);
            }
        });

        KeyMapping.resetMapping();
    }

    public static void save() {

        if (!KEYBIND_FILE.exists()) {
            try {
                KEYBIND_FILE.createNewFile();
            } catch (IOException exception) {
                LOGGER.error("Failed to save server's keybinds!", exception);
            }
        }

        SavedServerKeys serverKeys = MoreObjects.firstNonNull(new Toml().read(KEYBIND_FILE).to(SavedServerKeys.class), new SavedServerKeys());

        ServerKeys.getKeybinds().forEach(serverKey -> serverKeys.changeProperties(
                serverKey.getId().getNamespace(),
                serverKey.getId().getPath(),
                serverKey.getBoundKeyCode().getName(),
                serverKey.getBoundModifiers().stream().map(ModifierKey::getId).toList()
        ));

        try {
            new TomlWriter.Builder()
                    .indentValuesBy(2)
                    .build().write(serverKeys, KEYBIND_FILE);
        } catch (IOException exception) {
            LOGGER.error("Failed to save server's keybinds!", exception);
        }

    }

}
