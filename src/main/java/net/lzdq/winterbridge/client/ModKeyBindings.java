package net.lzdq.winterbridge.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
	public static final ModKeyBindings INSTANCE = new ModKeyBindings();
	private ModKeyBindings(){}
	private static final String CATEGORY = "key.categories." + WinterBridge.MODID;
	private static final String CATEGORY_NORMAL = "key.categories." + WinterBridge.MODID + " NORMAL";
	private static final String CATEGORY_RUSHING = "key.categories." + WinterBridge.MODID + " RUSHING";
	private static final String CATEGORY_INVENTORY = "key.categories." + WinterBridge.MODID + " INVENTORY";
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
	public final KeyMapping KEY_BLOCKS = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_blocks",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY
	);
	public final KeyMapping KEY_PLAYER_NAMES = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_player_names",
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
	public final KeyMapping KEY_NINJA_INC1 = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_ninja_inc1",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_F7,
			CATEGORY
	);
	public final KeyMapping KEY_NINJA_INC3 = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_ninja_inc3",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_F8,
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
	public final KeyMapping KEY_GAPPLE = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_gapple",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY
	);
	public final KeyMapping KEY_E = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_e",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY
	);
	public final KeyMapping KEY_LADDER = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_ladder",
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
	public final KeyMapping KEY_CUSTOM = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_custom",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY
	);
	// Below are only for normal mode
	public final KeyMapping KEY_NORMAL_TNT = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_normal_tnt",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_NORMAL
	);
	public final KeyMapping KEY_NORMAL_DROP_MONEY = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_normal_drop_money",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_NORMAL
	);
	// Below are only for rushing mode
	public final KeyMapping KEY_RUSHING_TNT = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_rushing_tnt",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_RUSHING
	);
	public final KeyMapping KEY_RUSHING_BLOCKIN = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_rushing_blockin",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_RUSHING
	);
	public final KeyMapping KEY_RUSHING_HARD_BLOCK = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_rushing_hard_block",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_RUSHING
	);
	// Below are only for inventory
	public final KeyMapping KEY_STORE_MONEY = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_store_money",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_INVENTORY
	);
	public final KeyMapping KEY_GET_MONEY = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_get_money",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_INVENTORY
	);
	public final KeyMapping KEY_TIDY_SLOTS = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_tidy_slots",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_INVENTORY
	);
	public final KeyMapping KEY_SET_CUSTOM = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_set_custom",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY_INVENTORY
	);
	public final KeyMapping KEY_UNSET_CUSTOM = new KeyMapping(
			"key." + WinterBridge.MODID + ".key_unset_custom",
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY
	);
}
