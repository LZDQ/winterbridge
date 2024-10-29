package net.lzdq.winterbridge.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.client.KeyMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
	public static final ModKeyBindings INSTANCE = new ModKeyBindings();
	public static final String CATEGORY = "key.categories." + WinterBridge.MODID;
	public static final String CATEGORY_NORMAL = "key.categories." + WinterBridge.MODID + ".normal";
	public static final String CATEGORY_RUSHING = "key.categories." + WinterBridge.MODID + ".rushing";
	public static final String CATEGORY_INVENTORY = "key.categories." + WinterBridge.MODID + ".inventory";
	public Map<String, KeyMapping> keys = new LinkedHashMap<>();
	private void add(String name, String category, int keyCode){
		keys.put(name, new KeyMapping("key." + WinterBridge.MODID + "." + name,
					InputConstants.Type.KEYSYM,
					keyCode,
					category));
	}
	private void add(String name, String category){
		add(name, category, InputConstants.UNKNOWN.getValue());
	}
	public KeyMapping get(String name){
		return keys.get(name);
	}
	private ModKeyBindings(){
		add("sort", CATEGORY, GLFW.GLFW_KEY_F4);
		add("cancel", CATEGORY, GLFW.GLFW_KEY_GRAVE_ACCENT);
		add("blocks", CATEGORY);
		add("player_names", CATEGORY);
		add("ninja", CATEGORY, GLFW.GLFW_KEY_F6);
		add("ninja_inc3", CATEGORY, GLFW.GLFW_KEY_F7);
		add("ninja_diag", CATEGORY, GLFW.GLFW_KEY_Y);
		add("change_cheat_mode", CATEGORY, GLFW.GLFW_KEY_N);
		add("change_rushing_mode", CATEGORY, GLFW.GLFW_KEY_M);
		add("fireball", CATEGORY, GLFW.GLFW_KEY_R);
		add("epearl", CATEGORY);
		add("egg", CATEGORY);
		add("gapple", CATEGORY, GLFW.GLFW_KEY_X);
		add("ladder_or_def", CATEGORY);
		add("bow", CATEGORY);
		add("potions", CATEGORY);
		add("func_e", CATEGORY);
		add("custom", CATEGORY);
		add("auto_login", CATEGORY);
		add("auto_who", CATEGORY);
		add("auto_send_inc", CATEGORY);
		
		// Below are only for normal mode
		add("normal_tnt", CATEGORY_NORMAL);
		add("normal_drop_money", CATEGORY_NORMAL);

		// Below are only for rushing mode
		add("rushing_tnt", CATEGORY_RUSHING);
		add("rushing_blockin", CATEGORY_RUSHING);
		add("rushing_hardest_block", CATEGORY_RUSHING);
		
		// Below are only for inventory
		add("store_money", CATEGORY_INVENTORY);
		add("get_money", CATEGORY_INVENTORY);
		add("tidy_slots", CATEGORY_INVENTORY);
		add("set_custom", CATEGORY_INVENTORY);
		add("unset_custom", CATEGORY_INVENTORY);
		add("second_slot", CATEGORY_INVENTORY);
		add("last_slot", CATEGORY_INVENTORY);
	}
}
