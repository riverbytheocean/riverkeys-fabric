package io.github.riverbytheocean.mods.riverkeys;

import io.github.riverbytheocean.mods.riverkeys.payloads.AddKeyPayload;
import io.github.riverbytheocean.mods.riverkeys.payloads.HandshakePayload;
import io.github.riverbytheocean.mods.riverkeys.payloads.KeyPressPayload;
import io.github.riverbytheocean.mods.riverkeys.payloads.LoadKeysPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Riverkeys implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("riverkeys");

	@Override
	public void onInitialize() {
	}
}