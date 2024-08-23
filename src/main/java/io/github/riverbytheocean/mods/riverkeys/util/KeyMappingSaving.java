package io.github.riverbytheocean.mods.riverkeys.util;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class KeyMappingSaving {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    public static final File KEYBIND_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "riverkeys.toml");

    public static void load() {
        try {
            if (!KEYBIND_FILE.exists()) return;

            CompoundTag nbtCompound = new CompoundTag();
            BufferedReader bufferedReader = Files.newReader(KEYBIND_FILE, Charsets.UTF_8);

            try {
                bufferedReader.lines().forEach((line) -> {
                    try {
                        Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();
                        nbtCompound.putString(iterator.next(), iterator.next());
                    } catch (Exception exception) {
                        LOGGER.warn("Skipping bad option: {}", line);
                    }

                });
            } catch (Throwable throwable) {
                try {
                    bufferedReader.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }

                throw throwable;
            }
            bufferedReader.close();

            for (ServerKey serverKey : ServerKeys.getKeybinds()) {
                String key = serverKey.getId().toString().replace(":", "+");

                String defKey = serverKey.getBoundKeyCode().getName();
                String keybind = MoreObjects.firstNonNull(nbtCompound.contains(key) ? nbtCompound.getString(key) : null, defKey);

                Set<ModifierKey> modifiers = new HashSet<>(serverKey.getModifiers());
                for (ModifierKey modifier : ModifierKey.ALL) {
                    String modKey = key + "_" + modifier.getId();
                    if (nbtCompound.contains(modKey)) modifiers.add(modifier);
                    else modifiers.remove(modifier);
                }

                if (!defKey.equals(keybind) || !modifiers.containsAll(serverKey.getModifiers())) {
                    serverKey.setBoundKey(InputConstants.getKey(keybind), false);
                    serverKey.setBoundModifiers(modifiers);
                }
            }

            KeyMapping.resetMapping();
        } catch (Exception exception) {
            LOGGER.error("Failed to load server's bindings", exception);
        }
    }

    public static void save() {

        if (!KEYBIND_FILE.exists()) {
            try {
                KEYBIND_FILE.createNewFile();
            } catch (IOException exception) {
                LOGGER.error("Failed to save server's keybinds!", exception);
            }
        }

        Toml toml = new Toml().read(KEYBIND_FILE);
        Map<String, Object> keyMappings = toml.toMap();
        Map<String, Object> finalKeyMappings = new HashMap<>();

        keyMappings.forEach((key, value) -> {
            finalKeyMappings.put(key.replaceAll("^\"|\"$", ""), value);
        });

        ServerKeys.getKeybinds().forEach(serverKey -> {
            finalKeyMappings.put(serverKey.getId().toString(), serverKey.getBoundKeyCode().getName());
        });

        try {
            new TomlWriter().write(finalKeyMappings, KEYBIND_FILE);
        } catch (IOException exception) {
            LOGGER.error("Failed to save server's keybinds!", exception);
        }

    }

}
