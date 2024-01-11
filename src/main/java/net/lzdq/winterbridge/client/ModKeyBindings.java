package net.lzdq.winterbridge.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.lzdq.winterbridge.BridgeMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

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

    public final KeyMapping KEY_RECORD = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_record",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            CATEGORY
    );

    public final KeyMapping KEY_CANCEL = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_cancel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            CATEGORY
    );
    public final KeyMapping KEY_BLOCKIN = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_blockin",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            CATEGORY
    );

    public final KeyMapping KEY_PLAYERS = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_players",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );
    public final KeyMapping KEY_NINJA = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_ninjabridge",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F6,
            CATEGORY
    );

    public final KeyMapping KEY_INC3 = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_ninja_inc3",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            CATEGORY
    );

    public final KeyMapping KEY_INC2 = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_ninja_inc2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            CATEGORY
    );

    public final KeyMapping KEY_DIAGONAL = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_diagonal",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            CATEGORY
    );

    public final KeyMapping KEY_DIAGINC = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_diaginc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            CATEGORY
    );

    public final KeyMapping KEY_TELLY = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_tellybridge",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            CATEGORY
    );

    public final KeyMapping KEY_GOD = new KeyMapping(
            "key." + BridgeMod.MODID + ".key_godbridge",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    );
}
