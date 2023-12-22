package net.lzdq.winterbridge.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.lzdq.winterbridge.BridgeMod;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
    public static final ModKeyBindings INSTANCE = new ModKeyBindings();
    private ModKeyBindings(){}
    private static final String CATEGORY = "key.categories." + BridgeMod.MODID;
    public final KeyMapping KEY_SORT = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_sort", // Will be localized using this translation key
            InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
            GLFW.GLFW_KEY_F4, // Default key
            CATEGORY // Mapping will be in the misc category
    );
    public final KeyMapping KEY_TEST = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_test",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F12,
            CATEGORY
    );
}
