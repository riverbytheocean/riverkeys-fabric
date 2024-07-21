package io.github.riverbytheocean.mods.riverkeys.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.riverkeys.util.KeyMappingSaving;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

public class ServerKeybindOptions extends OptionsSubScreen {

    private static final Component TITLE = Component.translatable("riverkeys.controls.title");
    @Nullable
    public ServerKey selectedKey;
    public long lastKeySelection;
    private ServerKeyList serverKeyList;
    private Button resetButton;

    public ServerKeybindOptions(Screen screen, Options options) {
        super(screen, options, TITLE);
    }

    protected void addContents() {
        this.serverKeyList = this.layout.addToContents(new ServerKeyList(this, this.minecraft));
    }

    protected void addOptions() {
    }

    protected void addFooter() {
        this.resetButton = Button.builder(Component.translatable("controls.resetAll"), (button) -> {
            for (ServerKey keyBinding : ServerKeys.getKeybinds()) {
                keyBinding.setBoundKey(keyBinding.getKeyCode(), false);
                keyBinding.resetBoundModifiers();
            }
            this.serverKeyList.resetMappingAndUpdateButtons();
            KeyMapping.resetMapping();
        }).build();
        LinearLayout linearLayout = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        linearLayout.addChild(this.resetButton);
        linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.onClose();
        }).build());
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        this.serverKeyList.updateSize(this.width, this.layout);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKey != null) {
            selectedKey.setBoundKey(InputConstants.Type.MOUSE.getOrCreate(button), true);
            KeyMappingSaving.save();

            this.selectedKey = null;
            this.serverKeyList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selectedKey != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                selectedKey.setBoundKey(InputConstants.UNKNOWN, false);
                selectedKey.setBoundModifiers(new HashSet<>());
            } else if (isModifier(keyCode)) return super.keyPressed(keyCode, scanCode, modifiers);
            else selectedKey.setBoundKey(InputConstants.getKey(keyCode, scanCode), true);
            KeyMappingSaving.save();

            this.selectedKey = null;
            this.serverKeyList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        boolean canReset = false;

        for (ServerKey serverKey : ServerKeys.getKeybinds()) {
            if (serverKey.hasChanged()) {
                canReset = true;
                break;
            }
        }

        this.resetButton.active = canReset;
    }

    private boolean isModifier(int code) {
        for (ModifierKey modifier : ModifierKey.ALL)
            if (modifier.getCode() == code) return true;
        return false;
    }

}
