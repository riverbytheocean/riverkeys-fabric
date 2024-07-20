package io.github.riverbytheocean.mods.riverkeys.mixin.client;

import io.github.riverbytheocean.mods.riverkeys.screen.ServerKeysButton;
import io.github.riverbytheocean.mods.riverkeys.screen.ServerKeysOptions;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {

    @Shadow @Final private Options options;

    protected OptionsScreenMixin(Component component) { super(component); }

    @Inject(method = "init", at = @At("TAIL"))
    protected void initMainButton(CallbackInfo ci) {

        if (minecraft == null || minecraft.isSingleplayer() || ServerKeys.getKeybinds().isEmpty()) return;
        this.addRenderableWidget(new ServerKeysButton(this.width / 2 + 158, this.height / 6 + 72 - 6,
                (button) -> this.minecraft.setScreen(new ServerKeysOptions(this, this.options))));

    }
}
