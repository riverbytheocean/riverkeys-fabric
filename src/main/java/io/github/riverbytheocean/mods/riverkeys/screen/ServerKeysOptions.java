package io.github.riverbytheocean.mods.riverkeys.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.riverkeys.util.KeyMappingSaving;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class ServerKeysOptions extends OptionsSubScreen {

    public ServerKey focusedMKey;
    private ControlsListWidget keyBindingListWidget;
    private Button resetButton;

    public ServerKeysOptions(Screen parent, Options options) {
        super(parent, options, Component.translatable("riverkeys.controls.title"));
    }

    protected void init() {
        if (minecraft == null) return;
        this.keyBindingListWidget = new ControlsListWidget(this, this.minecraft);
        this.addWidget(this.keyBindingListWidget);
        this.resetButton = this.addRenderableWidget(Button.builder(Component.translatable("controls.resetAll"), (button) -> {
            for (ServerKey keyBinding : ServerKeys.getKeybinds()) {
                keyBinding.setBoundKey(keyBinding.getKeyCode(), false);
                keyBinding.resetBoundModifiers();
            }
            KeyMapping.resetMapping();
        }).bounds(this.width / 2 - 155, this.height - 29, 150, 20).build());


        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.minecraft.setScreen(this.lastScreen))
                .bounds(this.width / 2 - 155 + 160, this.height - 29, 150, 20).build());
    }

    @Override
    protected void addOptions() {}

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focusedMKey != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                focusedMKey.setBoundKey(InputConstants.UNKNOWN, false);
                focusedMKey.setBoundModifiers(new HashSet<>());
            } else if (isModifier(keyCode)) return super.keyPressed(keyCode, scanCode, modifiers);
            else focusedMKey.setBoundKey(InputConstants.getKey(keyCode, scanCode), true);
            KeyMappingSaving.save();

            this.focusedMKey = null;
            KeyMapping.resetMapping();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private boolean isModifier(int code) {
        for (ModifierKey modifier : ModifierKey.ALL)
            if (modifier.getCode() == code) return true;
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.focusedMKey != null) {
            focusedMKey.setBoundKey(InputConstants.Type.MOUSE.getOrCreate(button), true);
            KeyMappingSaving.save();

            this.focusedMKey = null;
            KeyMapping.resetMapping();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderBackground(guiGraphics, i, j, f);
        this.keyBindingListWidget.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        boolean canReset = false;

        for (ServerKey serverKey : ServerKeys.getKeybinds()) {
            if (serverKey.hasChanged()) {
                canReset = true;
                break;
            }
        }

        this.resetButton.active = canReset;
        super.render(guiGraphics, i, j, f);
    }
}
