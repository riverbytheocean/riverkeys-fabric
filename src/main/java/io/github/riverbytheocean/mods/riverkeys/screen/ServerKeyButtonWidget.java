package io.github.riverbytheocean.mods.riverkeys.screen;

import io.github.riverbytheocean.mods.riverkeys.ServerKeysClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class ServerKeyButtonWidget extends Button {

    public ServerKeyButtonWidget(int x, int y, int width, int height, Screen screen) {
        super(
                x,
                y,
                width,
                height,
                Component.empty(),
                button -> Minecraft.getInstance().setScreen(screen),
                DEFAULT_NARRATION
        );
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.isHoveredOrFocused() ? ServerKeysClient.from("selected_button.png") : ServerKeysClient.from("unselected_button.png"),
                this.getX(), this.getY(),
                20, 20, 20, 20, 20, 20
        );
    }
}
