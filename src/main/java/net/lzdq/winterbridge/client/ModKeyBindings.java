package net.lzdq.winterbridge.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
    public static final ModKeyBindings INSTANCE = new ModKeyBindings();
    private ModKeyBindings(){}
    private static final String CATEGORY = "key.categories." + WinterBridge.MODID;
    public final KeyMapping KEY_SORT = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_sort", // Will be localized using this translation key
            InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
            GLFW.GLFW_KEY_F4, // Default key
            CATEGORY // Mapping will be in the misc category
    );
    public final KeyMapping KEY_TEST = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_test",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F12,
            CATEGORY
    );

    public final KeyMapping KEY_RECORD = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_record",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            CATEGORY
    );

    public final KeyMapping KEY_CANCEL = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_cancel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            CATEGORY
    );
    public final KeyMapping KEY_BLOCKIN = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_blockin",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            CATEGORY
    );

    public final KeyMapping KEY_PLAYERS = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_players",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );
    public final KeyMapping KEY_NINJA = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_ninja",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F6,
            CATEGORY
    );

    public final KeyMapping KEY_NINJA_INC = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_ninja_inc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            CATEGORY
    );

    public final KeyMapping KEY_NINJA_DIAG = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_ninja_diag",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            CATEGORY
    );

    public final KeyMapping KEY_NINJA_DIAG_INC = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_ninja_diag_inc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            CATEGORY
    );

    public final KeyMapping KEY_TELLY = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_tellybridge",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            CATEGORY
    );

    public final KeyMapping KEY_GOD = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_godbridge",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            CATEGORY
    );
}
