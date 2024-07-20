package io.github.riverbytheocean.mods.riverkeys.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ServerKeysButton implements Renderable, GuiEventListener, NarratableEntry {

    public static final ResourceLocation WIDGETS_TEXTURE = ResourceLocation.fromNamespaceAndPath("riverkeys", "textures/gui/riverkeys_button.png");
    @Getter
    private final Component message = Component.translatable("riverkeys.thebutton");
    public int x;
    public int y;
    protected boolean hovered;
    public boolean active = true;
    public boolean visible = true;
    @Getter
    private boolean focused;

    protected final PressAction onPress;

    public ServerKeysButton(int x, int y, PressAction onPress) {
        this.x = x;
        this.y = y;
        this.onPress = onPress;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 20 && mouseY < this.y + 20;
            this.renderButton(context);
        }
    }

    protected MutableComponent getNarrationMessage() {
        return getNarrationMessage(this.getMessage());
    }

    public static MutableComponent getNarrationMessage(Component message) {
        return Component.translatable("gui.narrate.button", message);
    }

    public void renderButton(GuiGraphics context) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        context.blit(WIDGETS_TEXTURE, this.x, this.y, 0, this.isHovered() ? 20 : 0, 20, 20);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean click = this.clicked(mouseX, mouseY);
                if (click) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onPress.onPress(this);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isValidClickButton(button);
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.isValidClickButton(button);
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + 20) && mouseY < (double) (this.y + 20);
    }

    public boolean isHovered() {
        return this.hovered || this.focused;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + 20) && mouseY < (double) (this.y + 20);
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public boolean isActive() {
        return this.visible && this.active;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.active && this.visible) {
            if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
                return false;
            } else {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress.onPress(this);
                return true;
            }
        } else {
            return false;
        }
    }

    public NarrationPriority narrationPriority() {
        if (this.focused) {
            return NarrationPriority.FOCUSED;
        } else {
            return this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
        }
    }


    public void updateNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
            } else {
                builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
            }
        }
    }

    public interface PressAction {
        void onPress(ServerKeysButton button);
    }

}
