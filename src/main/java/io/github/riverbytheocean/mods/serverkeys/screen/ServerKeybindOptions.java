package io.github.riverbytheocean.mods.serverkeys.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.serverkeys.keymappings.ServerKey;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ServerKeybindOptions extends OptionsSubScreen {

    @Nullable
    public ServerKey selectedKey;
    public long lastKeySelection;
    private ServerKeyList serverKeyList;
    private Button resetButton;

    public ServerKeybindOptions(Screen lastScreen, Options options) {
        super(
                lastScreen,
                options,
                Component.translatable("riverkeys.controls.title")
        );
    }

    protected void addContents() {
        this.serverKeyList = this.layout.addToContents(new ServerKeyList(this, this.minecraft));
    }

    protected void addOptions() {
    }

    protected void addFooter() {
        this.resetButton = Button.builder(Component.translatable("controls.resetAll"), (button) -> {
            for(KeyMapping keyMapping : this.options.keyMappings) {
                keyMapping.setKey(keyMapping.getDefaultKey());
            }

            this.serverKeyList.resetMappingAndUpdateButtons();
        }).build();
        LinearLayout linearLayout = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        linearLayout.addChild(this.resetButton);
        linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).build());
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        this.serverKeyList.updateSize(this.width, this.layout);
    }

    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (this.selectedKey != null) {
            this.selectedKey.setBoundKey(InputConstants.Type.MOUSE.getOrCreate(mouseButtonEvent.button()), true);
            this.selectedKey = null;
            this.serverKeyList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.mouseClicked(mouseButtonEvent, bl);
        }
    }

    public boolean keyPressed(KeyEvent keyEvent) {
        if (this.selectedKey != null) {
            if (keyEvent.isEscape()) {
                this.selectedKey.setBoundKey(InputConstants.UNKNOWN, true);
            } else {
                this.selectedKey.setBoundKey(InputConstants.getKey(keyEvent), true);
            }

            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            this.serverKeyList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.keyPressed(keyEvent);
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        boolean bl = false;

        for(KeyMapping keyMapping : this.options.keyMappings) {
            if (!keyMapping.isDefault()) {
                bl = true;
                break;
            }
        }

        this.resetButton.active = bl;
    }
}
