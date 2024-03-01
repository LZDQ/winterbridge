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
    public final KeyMapping KEY_CANCEL = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_cancel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            CATEGORY
    );
    public final KeyMapping KEY_BLOCKIN = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_blockin",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_BLOCKS = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_blocks",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_PLAYERS = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_players",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
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
    public final KeyMapping KEY_CHEAT_MODE = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_cheatmode",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            CATEGORY
    );
    public final KeyMapping KEY_GOD = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_godbridge",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_FIREBALL = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_fireball",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_EPEARL = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_epearl",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_EGG = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_egg",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_TOWER = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_tower",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_GAPPLE = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_gapple",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_HARD_BLOCK = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_hard_block",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_AUTO_TOOL = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_auto_tool",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_AUTO_LOGIN = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_auto_login",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public final KeyMapping KEY_AUTO_WHO = new KeyMapping(
            "key." + WinterBridge.MODID + ".key_auto_who",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
}
