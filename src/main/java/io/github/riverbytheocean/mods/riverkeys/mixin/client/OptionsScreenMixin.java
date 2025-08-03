package io.github.riverbytheocean.mods.riverkeys.mixin.client;

import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.riverkeys.screen.ServerKeyButtonWidget;
import io.github.riverbytheocean.mods.riverkeys.screen.ServerKeybindOptions;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {

    @Shadow @Final private Options options;

    @Unique
    ServerKeyButtonWidget button;

    protected OptionsScreenMixin(Component component) { super(component); }

    @Inject(method = "init", at = @At("TAIL"))
    protected void initMainButton(CallbackInfo ci) {

        if (minecraft == null || minecraft.isSingleplayer() || ServerKeys.getKeybinds().isEmpty()) return;

        initButton();

    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void reloadButton(CallbackInfo ci) {
        initButton();
    }

    @Unique
    private void initButton() {
        Button controls = this.children().stream()
                .filter(c -> c instanceof Button b && b.getMessage().equals(Component.translatable("options.controls")))
                .map(Button.class::cast)
                .findFirst()
                .orElseGet(() -> Button.builder(Component.empty(), b -> {}).build());

        if (this.button == null) {
            this.button = this.addRenderableWidget(new ServerKeyButtonWidget(
                    controls.getRight() + 2, controls.getY(),
                    20, 20,
                    new ServerKeybindOptions(this, this.options)
            ));
        }

        button.setPosition(controls.getRight() + 2, controls.getY());
    }
}
