package io.github.riverbytheocean.mods.serverkeys.screen;

import com.google.common.collect.ImmutableList;
import io.github.riverbytheocean.mods.serverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.serverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.serverkeys.keymappings.ServerKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public class ServerKeyList extends ContainerObjectSelectionList<ServerKeyList.Entry> {

    private static final int ITEM_HEIGHT = 20;
    final ServerKeybindOptions keyBindsScreen;
    private int maxNameWidth;

    public ServerKeyList(ServerKeybindOptions serverKeybindOptions, Minecraft minecraft) {
        super(minecraft, serverKeybindOptions.width, serverKeybindOptions.layout.getContentHeight(), serverKeybindOptions.layout.getHeaderHeight(), 20);
        this.keyBindsScreen = serverKeybindOptions;
        KeyMapping.Category category = null;

        for (ServerKey serverKey : ServerKeys.getCategorySortedKeybinds()) {
            //String keyCat = serverKey.getCategory();
//            if (category == null || !keyCat.equals(category.id().getPath())) {
//                category = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath("serverkeys", keyCat.toLowerCase()));
//                this.addEntry(new CategoryEntry(category));
//            }

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
        this.children().forEach(Entry::refreshEntry);
    }

    public int getRowWidth() {
        return 340;
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<ServerKeyList.Entry> {
        abstract void refreshEntry();
    }

    @Environment(EnvType.CLIENT)
    public class CategoryEntry extends Entry {
        private final FocusableTextWidget categoryName;

        public CategoryEntry(final KeyMapping.Category category) {
            this.categoryName = new FocusableTextWidget(
                    ServerKeyList.this.getRowWidth(),
                    category.label(),
                    ServerKeyList.this.minecraft.font,
                    false,
                    FocusableTextWidget.BackgroundFill.ON_FOCUS, 4
            );
        }

        public void renderContent(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
            FocusableTextWidget var10000 = this.categoryName;
            int var10001 = ServerKeyList.this.width / 2 - this.categoryName.getWidth() / 2;
            int var10002 = this.getContentBottom();
            Objects.requireNonNull(ServerKeyList.this.minecraft.font);
            var10000.setPosition(var10001, var10002 - 9 - 1);
            this.categoryName.render(guiGraphics, i, j, f);
        }

        public List<? extends GuiEventListener> children() {
            return List.of(this.categoryName);
        }

        public List<? extends NarratableEntry> narratables() {
            return List.of(this.categoryName);
        }

        protected void refreshEntry() {
        }
    }

    @Environment(EnvType.CLIENT)
    public class KeyEntry extends Entry {
        private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
        private static final int PADDING = 10;
        private final ServerKey key;
        private final Component name;
        private final Button changeButton;
        private final Button resetButton;
        private boolean hasCollision = false;

        KeyEntry(final ServerKey key, final Component name) {
            this.key = key;
            this.name = name;
            this.changeButton = Button.builder(name, (button) -> {
                ServerKeyList.this.keyBindsScreen.selectedKey = key;
                ServerKeyList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 75, 20).createNarration((supplier) -> key.isUnbound() ? Component.translatable("narrator.controls.unbound", new Object[]{name}) : Component.translatable("narrator.controls.bound", new Object[]{name, supplier.get()})).build();
            this.resetButton = Button.builder(RESET_BUTTON_TITLE, (button) -> {
                key.setBoundKey(key.getKeyCode(), true);
                ServerKeyList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 50, 20).createNarration((supplier) -> Component.translatable("narrator.controls.reset", new Object[]{name})).build();
            this.refreshEntry();
        }

        public void renderContent(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
            int k = ServerKeyList.this.scrollBarX() - this.resetButton.getWidth() - 10;
            int l = this.getContentY() - 2;
            this.resetButton.setPosition(k, l);
            this.resetButton.render(guiGraphics, i, j, f);
            int m = k - 5 - this.changeButton.getWidth();
            this.changeButton.setPosition(m, l);
            this.changeButton.render(guiGraphics, i, j, f);
            Font var10001 = ServerKeyList.this.minecraft.font;
            Component var10002 = this.name;
            int var10003 = this.getContentX();
            int var10004 = this.getContentYMiddle();
            Objects.requireNonNull(ServerKeyList.this.minecraft.font);
            guiGraphics.drawString(var10001, var10002, var10003, var10004 - 9 / 2, -1);
            if (this.hasCollision) {
                int o = this.changeButton.getX() - 6;
                guiGraphics.fill(o, this.getContentY() - 1, o + 3, this.getContentBottom(), -256);
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
            for (ModifierKey modifier : this.key.getBoundModifiers()) {
                message.append(Component.translatable(modifier.getTranslationKey()));
                message.append(Component.literal(" + "));
            }
            message.append(this.key.getBoundKeyCode().getDisplayName());
            this.changeButton.setMessage(message);
            //this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            this.resetButton.active = !this.key.isDefault();
            this.hasCollision = false;
            MutableComponent mutableComponent = Component.empty();
            if (!this.key.isUnbound()) {
                for(KeyMapping keyMapping : ServerKeyList.this.minecraft.options.keyMappings) {
                    if (keyMapping.getDefaultKey() != this.key.getKeyCode() && this.key.same(keyMapping) && (!keyMapping.isDefault() || !this.key.isDefault())) {
                        if (this.hasCollision) {
                            mutableComponent.append(", ");
                        }

                        this.hasCollision = true;
                        mutableComponent.append(Component.translatable(keyMapping.getName()));
                    }
                }

                for (ServerKey key : ServerKeys.getKeybinds()) {
                    if (!key.equals(this.key) && key.getBoundKeyCode().equals(this.key.getBoundKeyCode())) {
                        if (key.testModifiers(this.key.getBoundModifiers())) {
                            this.hasCollision = true;
                            break;
                        }
                    }
                }
            }

            if (this.hasCollision) {
                this.changeButton
                        .setMessage(Component.literal("[ ")
                                .append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE))
                                .append(" ]").withStyle(ChatFormatting.YELLOW));
                this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", mutableComponent)));
            } else {
                this.changeButton.setTooltip(null);
            }

            if (ServerKeyList.this.keyBindsScreen.selectedKey == this.key) {
                this.changeButton
                        .setMessage(Component.literal("> ")
                                .append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                                .append(" <").withStyle(ChatFormatting.YELLOW));
            }

        }
    }

}
