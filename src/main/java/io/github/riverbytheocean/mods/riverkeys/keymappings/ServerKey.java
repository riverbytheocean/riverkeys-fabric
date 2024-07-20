package io.github.riverbytheocean.mods.riverkeys.keymappings;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.riverbytheocean.mods.riverkeys.util.network.KeyAddData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ServerKey {

    private final ResourceLocation id;
    private final String name, category;
    private final InputConstants.Key keyCode;
    private InputConstants.Key boundKeyCode;
    private final Set<ModifierKey> modifiers;
    @Setter
    private Set<ModifierKey> boundModifiers;

    public ServerKey(KeyAddData data) {
        this(data.getId(), data.getName(), data.getCategory(), InputConstants.Type.KEYSYM.getOrCreate(data.getDefKey()), data.getModifiers());
    }

    public ServerKey(ResourceLocation id, String name, String category, InputConstants.Key keyCode, int[] modifiers) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.keyCode = keyCode;
        this.boundKeyCode = keyCode;
        this.modifiers = ModifierKey.getFromArray(modifiers);
        this.boundModifiers = new HashSet<>(this.modifiers);
    }

    public void setBoundKey(InputConstants.Key key, boolean handleModifiers) {
        if (handleModifiers) {
            Set<ModifierKey> mods = new HashSet<>();
            for (ModifierKey modifier : ModifierKey.ALL)
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), modifier.getCode())) mods.add(modifier);
            setBoundModifiers(mods);
        }
        this.boundKeyCode = key;
    }

    public void resetBoundModifiers() {
        setBoundModifiers(new HashSet<>(this.modifiers));
    }

    public boolean hasChanged() {
        return !keyCode.equals(boundKeyCode) || !testModifiers(this.modifiers);
    }

    public boolean isUnbound() {
        return boundKeyCode.equals(InputConstants.UNKNOWN);
    }

    public boolean testModifiers() {
        for (ModifierKey key : boundModifiers)
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getCode())) return false;
        return true;
    }

    public boolean testModifiers(Set<ModifierKey> otherKeys) {
        return boundModifiers.containsAll(otherKeys) && otherKeys.containsAll(boundModifiers);
    }

}
