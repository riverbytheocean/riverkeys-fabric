package io.github.riverbytheocean.mods.riverkeys.screen;

import com.google.common.collect.ImmutableList;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ModifierKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKey;
import io.github.riverbytheocean.mods.riverkeys.keymappings.ServerKeys;
import io.github.riverbytheocean.mods.riverkeys.util.KeyMappingSaving;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ControlsListWidget extends ContainerObjectSelectionList<ControlsListWidget.Entry> {

    final ServerKeysOptions parent;
    int maxKeyNameLength;

    public ControlsListWidget(ServerKeysOptions parent, Minecraft client) {
        super(client, parent.width + 45, parent.height, 43, 30);
        this.parent = parent;
        String category = null;

        for (ServerKey serverKey : ServerKeys.getCategorySortedKeybinds()) {
            String keyCat = serverKey.getCategory();
            if (!keyCat.equals(category)) {
                category = keyCat;
                this.addEntry(new CategoryEntry(Component.literal(keyCat)));
            }

            Component component = Component.literal(serverKey.getName());
            int i = client.font.width(component);
            if (i > this.maxKeyNameLength) {
                this.maxKeyNameLength = i;
            }

            this.addEntry(new KeyBindingEntry(serverKey, component));
        }

    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    public class CategoryEntry extends Entry {
        final Component component;
        private final int componentWidth;

        public CategoryEntry(Component component) {
            this.component = component;
            this.componentWidth = ControlsListWidget.this.minecraft.font.width(this.component);
        }

        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            assert minecraft.screen != null;
            int width = (minecraft.screen.width / 2 - this.componentWidth / 2);
            int height = y + entryHeight;
            context.drawString(minecraft.font, this.component, width, height - 9 - 1, 16777215, false);
        }

        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput builder) {
                    builder.add(NarratedElementType.TITLE, CategoryEntry.this.component);
                }
            });
        }
    }

    public class KeyBindingEntry extends Entry {
        private final ServerKey serverKey;
        private final Component bindingName;
        private final Button editButton;
        private final Button resetButton;

        KeyBindingEntry(ServerKey serverKey, Component bindingName) {
            this.serverKey = serverKey;
            this.bindingName = bindingName;

            this.editButton = Button.builder(bindingName, (button) -> ControlsListWidget.this.parent.focusedMKey = serverKey)
                    .bounds(0, 0, 135, 20).createNarration(
                            supplier -> serverKey.isUnbound() ? Component.translatable("narrator.controls.unbound", bindingName) : Component.translatable(
                                    "narrator.controls.bound", bindingName, supplier.get())).build();

            this.resetButton = Button.builder(Component.translatable("controls.reset"), (button) -> {
                serverKey.setBoundKey(serverKey.getKeyCode(), false);
                serverKey.resetBoundModifiers();
                KeyMappingSaving.save();
                KeyMapping.resetMapping();
            }).bounds(0, 0, 50, 20).createNarration(supplier -> Component.translatable("narrator.controls.reset", bindingName)).build();
        }

        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean bl = parent.focusedMKey == this.serverKey;
            int width = x + 20 - maxKeyNameLength;
            int height = y + entryHeight / 2;
            context.drawString(minecraft.font, this.bindingName, width, height - 9 / 2, 16777215, false);

            this.resetButton.setX(x + 210);
            this.resetButton.setY(y);
            this.resetButton.active = this.serverKey.hasChanged();
            this.resetButton.render(context, mouseX, mouseY, tickDelta);
            this.editButton.setX(x + 65);
            this.editButton.setY(y);
            MutableComponent editMessage = Component.empty();
            for (ModifierKey modifier : this.serverKey.getBoundModifiers()) {
                editMessage.append(Component.translatable(modifier.getTranslationKey()));
                editMessage.append(Component.literal(" + "));
            }
            editMessage.append(this.serverKey.getBoundKeyCode().getDisplayName().plainCopy());
            editMessage = editMessage.copy();
            boolean bl2 = false;
            if (!this.serverKey.isUnbound()) {
                final List<KeyMapping> bindings = new ArrayList<>(List.of(minecraft.options.keyMappings));
                for (KeyMapping keyBinding : bindings) {
                    if (keyBinding.saveString().equals(serverKey.getBoundKeyCode().getDisplayName()) && serverKey.getBoundModifiers().isEmpty()) {
                        bl2 = true;
                        break;
                    }
                }
                for (ServerKey key : ServerKeys.getKeybinds()) {
                    if (!key.equals(serverKey) && key.getBoundKeyCode().equals(serverKey.getBoundKeyCode())) {
                        if (key.testModifiers(serverKey.getBoundModifiers())) {
                            bl2 = true;
                            break;
                        }
                    }
                }
            }

            if (bl) {
                this.editButton.setMessage((Component.literal("> ")).append(editMessage.withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            } else if (bl2) {
                this.editButton.setMessage(editMessage);
            } else this.editButton.setMessage(editMessage);

            this.editButton.render(context, mouseX, mouseY, tickDelta);
        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.editButton, this.resetButton);
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.editButton, this.resetButton);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.editButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            } else {
                return this.resetButton.mouseClicked(mouseX, mouseY, button);
            }
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    }

}
