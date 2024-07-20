package io.github.riverbytheocean.mods.riverkeys.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.keymappings.KeyPacketSending;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.riverkeys.util.network.KeyPressData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(KeyMapping.class)
public class KeyMappingInjects {

    @Unique
    private static final List<InputConstants.Key> riverkeys$pressedKeys = new ArrayList<>();

    @Inject(method = "set", at = @At("HEAD"))
    private static void input(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        // Only check for keybinds while outside a GUI
        if (Minecraft.getInstance().screen != null) return;

        KeyMapping keyMapping = KeyPacketSending.getKeyBinding(key);
        if (keyMapping != null) {
            ResourceLocation id = ServerKeys.cleanIdentifier(keyMapping.getName());
            if (ServerKeys.getVanillaKeys().contains(id)) riverkeys$registerPress(id, key, pressed);
        }

        for (ServerKey serverKey : ServerKeys.getModifierSortedKeybinds())
            if (key.equals(serverKey.getBoundKeyCode()) && serverKey.testModifiers()) riverkeys$registerPress(serverKey.getId(), key, pressed);
    }

    @Unique
    private static void riverkeys$registerPress(ResourceLocation id, InputConstants.Key key, boolean pressed) {
        // Check if the button was pressed or released
        if (pressed) {
            boolean held = riverkeys$pressedKeys.contains(key);
            // Check if it is already being pressed
            if (!held) {
                // Add it to the list of currently pressed keys
                riverkeys$pressedKeys.add(key);
                riverkeys$sendPacket(id, false);
            }
        } else {
            // Remove it from the list of currently pressed keys
            riverkeys$pressedKeys.remove(key);
            riverkeys$sendPacket(id, true);
        }
    }

    @Unique
    private static void riverkeys$sendPacket(ResourceLocation id, boolean release) {
        // Call the platform specific packet sending code
        KeyPacketSending.sendKey(new KeyPressData(id, release));
    }

}
