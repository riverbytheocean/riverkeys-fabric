package io.github.riverbytheocean.mods.riverkeys.util;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class KeyMappingSaving {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    private static final File KEYBIND_FILE = new File(Minecraft.getInstance().gameDirectory, "riverkeys.txt");

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
                String key = "serverkey_" + serverKey.getId().toString().replace(":", "+");

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
        try {
            final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(KEYBIND_FILE), StandardCharsets.UTF_8));

            try {
                for (ServerKey serverKey : ServerKeys.getKeybinds()) {
                    printWriter.print("serverkey_" + serverKey.getId().toString().replace(":", "+"));
                    printWriter.print(':');
                    printWriter.println(serverKey.getBoundKeyCode().getName());

                    for (ModifierKey modifier : serverKey.getBoundModifiers()) {
                        printWriter.print("serverkey_" + serverKey.getId().toString().replace(":", "+") + "_" + modifier.getId());
                        printWriter.print(':');
                        printWriter.println("true");
                    }
                }
            } catch (Throwable throwable) {
                try {
                    printWriter.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }

                throw throwable;
            }

            printWriter.close();
        } catch (Exception exception) {
            LOGGER.error("Failed to save server's keybinds!", exception);
        }
    }

}
