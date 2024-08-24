package io.github.riverbytheocean.mods.riverkeys.screen;

import com.google.common.collect.ImmutableList;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.riverkeys.util.saving.KeyMappingSaving;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServerKeyList extends ContainerObjectSelectionList<ServerKeyList.Entry> {

    final ServerKeybindOptions parent;

    final ServerKeybindOptions serverKeybindOptions;
    private int maxNameWidth;

    public ServerKeyList(ServerKeybindOptions serverKeybindOptions, Minecraft minecraft) {
        super(minecraft, serverKeybindOptions.width, serverKeybindOptions.layout.getContentHeight(), serverKeybindOptions.layout.getHeaderHeight(), 20);
        this.serverKeybindOptions = serverKeybindOptions;
        this.parent = serverKeybindOptions;

        String category = null;

        for (ServerKey serverKey : ServerKeys.getCategorySortedKeybinds()) {
            String keyCat = serverKey.getCategory();
            if (!keyCat.equals(category)) {
                category = keyCat;
                this.addEntry(new CategoryEntry(Component.literal(keyCat)));
            }

            Component component = Component.literal(serverKey.getName());
            int i = minecraft.font.width(component);
            if (i > this.maxNameWidth) {
                this.maxNameWidth = i;
            }

            this.addEntry(new KeyEntry(serverKey, component));
        }

    }

    public void resetMappingAndUpdateButtons() {
        KeyMapping.resetMapping();
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.children().forEach(ServerKeyList.Entry::refreshEntry);
    }

    public int getRowWidth() {
        return 340;
    }

    @Environment(EnvType.CLIENT)
    public class CategoryEntry extends ServerKeyList.Entry {
        final Component name;
        private final int width;

        public CategoryEntry(final Component component) {
            this.name = component;
            this.width = ServerKeyList.this.minecraft.font.width(this.name);
        }

        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            Font var10001 = ServerKeyList.this.minecraft.font;
            Component var10002 = this.name;
            int var10003 = ServerKeyList.this.width / 2 - this.width / 2;
            int var10004 = j + m;
            Objects.requireNonNull(ServerKeyList.this.minecraft.font);
            guiGraphics.drawString(var10001, var10002, var10003, var10004 - 9 - 1, -1, false);
        }

        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
            return null;
        }

        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput narrationElementOutput) {
                    narrationElementOutput.add(NarratedElementType.TITLE, ServerKeyList.CategoryEntry.this.name);
                }
            });
        }

        protected void refreshEntry() {
        }
    }

    @Environment(EnvType.CLIENT)
    public class KeyEntry extends ServerKeyList.Entry {
        private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
        private final ServerKey serverKey;
        private final Component component;
        private final Button changeButton;
        private final Button resetButton;
        private boolean hasCollision = false;

        KeyEntry(final ServerKey serverKey, final Component component) {
            this.serverKey = serverKey;
            this.component = component;

            this.changeButton = Button.builder(component, (button) -> {
                ServerKeyList.this.serverKeybindOptions.selectedKey = serverKey;
                ServerKeyList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 75, 20).createNarration((supplier) -> {
                return serverKey.isUnbound() ? Component.translatable("narrator.controls.unbound", new Object[]{component}) : Component.translatable("narrator.controls.bound", new Object[]{component, supplier.get()});
            }).build();

            this.resetButton = Button.builder(RESET_BUTTON_TITLE, (button) -> {
                serverKey.setBoundKey(serverKey.getKeyCode(), false);
                serverKey.resetBoundModifiers();
                KeyMappingSaving.save();
                ServerKeyList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 50, 20).createNarration(supplier -> Component.translatable("narrator.controls.reset", component)).build();

            this.refreshEntry();
        }

        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            int p = ServerKeyList.this.getScrollbarPosition() - this.resetButton.getWidth() - 10;
            int q = j - 2;
            this.resetButton.setPosition(p, q);
            this.resetButton.render(guiGraphics, n, o, f);
            int r = p - 5 - this.changeButton.getWidth();
            this.changeButton.setPosition(r, q);
            this.changeButton.render(guiGraphics, n, o, f);
            Font var10001 = ServerKeyList.this.minecraft.font;
            int var10004 = j + m / 2;
            Objects.requireNonNull(ServerKeyList.this.minecraft.font);
            guiGraphics.drawString(var10001, this.component, k, var10004 - 9 / 2, -1);
            if (this.hasCollision) {
                int t = this.changeButton.getX() - 6;
                guiGraphics.fill(t, j - 1, t + 3, j + m, -65536);
            }

        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.changeButton, this.resetButton);
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.changeButton, this.resetButton);
        }

        protected void refreshEntry() {
            MutableComponent message = Component.empty();
            for (ModifierKey modifier : this.serverKey.getBoundModifiers()) {
                message.append(Component.translatable(modifier.getTranslationKey()));
                message.append(Component.literal(" + "));
            }
            message.append(this.serverKey.getBoundKeyCode().getDisplayName());
            this.changeButton.setMessage(message);
            this.resetButton.active = this.serverKey.hasChanged();
            this.hasCollision = false;
            MutableComponent mutableComponent = Component.empty();
            if (!this.serverKey.isUnbound()) {

                for(KeyMapping keyMapping : ServerKeyList.this.minecraft.options.keyMappings) {
                    if (keyMapping.getDefaultKey().getName().equals(this.serverKey.getName())) {
                        if (this.hasCollision) {
                            mutableComponent.append(", ");
                        }

                        this.hasCollision = true;
                        mutableComponent.append(Component.translatable(keyMapping.getName()));
                    }
                }

                for (ServerKey key : ServerKeys.getKeybinds()) {
                    if (!key.equals(serverKey) && key.getBoundKeyCode().equals(serverKey.getBoundKeyCode())) {
                        if (key.testModifiers(serverKey.getBoundModifiers())) {
                            this.hasCollision = true;
                            break;
                        }
                    }
                }

            }

            if (this.hasCollision) {
                this.changeButton.setMessage(Component.literal("[ ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
                this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", new Object[]{mutableComponent})));
            } else {
                this.changeButton.setTooltip((Tooltip)null);
            }

            if (ServerKeyList.this.serverKeybindOptions.selectedKey == this.serverKey) {
                this.changeButton.setMessage(Component.literal("> ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW));
            }

        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<ServerKeyList.Entry> {
        public Entry() {
        }

        abstract void refreshEntry();
    }


}
