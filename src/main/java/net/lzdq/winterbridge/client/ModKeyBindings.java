package net.lzdq.winterbridge.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
	public static final ModKeyBindings INSTANCE = new ModKeyBindings();
	public static final String CATEGORY = "key.categories." + WinterBridge.MODID;
	public static final String CATEGORY_INVENTORY = "key.categories." + WinterBridge.MODID + ".inventory";
	public Map<String, KeyMapping> keys = new LinkedHashMap<>();

	private void add(String name, String category, int keyCode, KeyConflictContext context) {
		KeyMapping key = new KeyMapping("key." + WinterBridge.MODID + "." + name,
				context, // The context goes here
				InputConstants.Type.KEYSYM,
				keyCode,
				category);
		keys.put(name, key);
	}

	private void add(String name, String category, KeyConflictContext context) {
		add(name, category, InputConstants.UNKNOWN.getValue(), context);
	}

	public KeyMapping get(String name) {
		return keys.get(name);
	}

	private ModKeyBindings() {
		// add("sort", CATEGORY, GLFW.GLFW_KEY_F4);
		add("cancel", CATEGORY, GLFW.GLFW_KEY_GRAVE_ACCENT, KeyConflictContext.IN_GAME);
		add("blocks", CATEGORY, KeyConflictContext.IN_GAME);
		add("player_names", CATEGORY, KeyConflictContext.IN_GAME);
		add("ninja", CATEGORY, GLFW.GLFW_KEY_F6, KeyConflictContext.IN_GAME);
		add("change_cheat_mode", CATEGORY, GLFW.GLFW_KEY_N, KeyConflictContext.IN_GAME);
		add("fireball", CATEGORY, GLFW.GLFW_KEY_R, KeyConflictContext.IN_GAME);
		add("epearl", CATEGORY, KeyConflictContext.IN_GAME);
		add("egg", CATEGORY, KeyConflictContext.IN_GAME);
		add("gapple", CATEGORY, GLFW.GLFW_KEY_X, KeyConflictContext.IN_GAME);
		add("ladder_or_def", CATEGORY, KeyConflictContext.IN_GAME);
		add("bow", CATEGORY, KeyConflictContext.IN_GAME);
		add("tnt", CATEGORY, KeyConflictContext.IN_GAME);
		add("potions", CATEGORY, KeyConflictContext.IN_GAME);
		add("func_e", CATEGORY, KeyConflictContext.IN_GAME);
		add("blockin", CATEGORY, KeyConflictContext.IN_GAME);
		// add("custom", CATEGORY, KeyConflictContext.IN_GAME);
		add("auto_login", CATEGORY, KeyConflictContext.IN_GAME);
		add("auto_who", CATEGORY, KeyConflictContext.IN_GAME);
		add("auto_send_inc", CATEGORY, KeyConflictContext.IN_GAME);
		add("ice_bridge", CATEGORY, KeyConflictContext.IN_GAME);

		// This one is special, both in game and gui
		add("drop_money", CATEGORY, KeyConflictContext.UNIVERSAL);

		// Below are only for inventory
		add("store_money", CATEGORY_INVENTORY, KeyConflictContext.GUI);
		add("get_money", CATEGORY_INVENTORY, KeyConflictContext.GUI);
		add("second_slot", CATEGORY_INVENTORY, KeyConflictContext.GUI);
		add("last_slot", CATEGORY_INVENTORY, KeyConflictContext.GUI);
	}
}
