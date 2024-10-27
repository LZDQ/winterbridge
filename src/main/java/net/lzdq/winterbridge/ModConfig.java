package net.lzdq.winterbridge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


@Mod.EventBusSubscriber(modid="winterbridge", bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec CONFIG;
	public static final ForgeConfigSpec.IntValue slot_sword, slot_block, slot_shear, slot_pickaxe, slot_axe,
			slot_gapple, slot_fireball, slot_tnt, slot_ladder, slot_kbstick, slot_egg, slot_tower,
			cheat_mode;
	public static final ForgeConfigSpec.DoubleValue ninja_side_dist, ninja_pitch, ninja_walk_dist;
	public static final ForgeConfigSpec.DoubleValue ninja_diag_pitch, ninja_diag_walk_dist;
	public static final ForgeConfigSpec.DoubleValue god_pitch, god_walk_dist;
	public static final ForgeConfigSpec.ConfigValue<List<Integer>> ninja_wait_tick;
	public static final ForgeConfigSpec.ConfigValue<List<Double>> yaw_var, pitch_var;
	public static final ForgeConfigSpec.ConfigValue<String> color_start_bridge, color_cancel_bridge, auto_login_command;
	public static final ForgeConfigSpec.DoubleValue spam_left_min, spam_left_max, sort_delay;
	public static final ForgeConfigSpec.IntValue blockin_rotate_tick;
	public static final ForgeConfigSpec.DoubleValue blockin_offset;
	public static final ForgeConfigSpec.IntValue delay_sword;

	static {
		BUILDER.push("General Settings");

		slot_sword = BUILDER
				.comment("Slot to place sword")
				.defineInRange("slot_sword", 0, -1, 8);

		slot_block = BUILDER
				.comment("Slot to place blocks")
				.defineInRange("slot_block", 4, -1, 8);

		slot_shear = BUILDER
				.comment("Slot to place shear")
				.defineInRange("slot_shear", 2, -1, 8);

		slot_pickaxe = BUILDER
				.comment("Slot to place pickaxe")
				.defineInRange("slot_pickaxe", 1, -1, 8);

		slot_axe = BUILDER
				.comment("Slot to place axe")
				.defineInRange("slot_axe", 3, -1, 8);

		slot_gapple = BUILDER
				.comment("Slot to place golden apple")
				.defineInRange("slot_gapple", 8, -1, 8);

		slot_fireball = BUILDER
				.comment("Slot to place fireball")
				.defineInRange("slot_fireball", 5, -1, 8);

		slot_tnt = BUILDER
				.comment("Slot to place tnt")
				.defineInRange("slot_tnt", -1, -1, 8);

		slot_ladder = BUILDER
				.comment("Slot to place ladder")
				.defineInRange("slot_ladder", -1, -1, 8);

		slot_kbstick = BUILDER
				.comment("Slot to place knock-back stick")
				.defineInRange("slot_kbstick", -1, -1, 8);

		slot_egg = BUILDER
				.comment("Slot to place bridge egg")
				.defineInRange("slot_egg", -1, -1, 8);

		slot_tower = BUILDER
				.comment("Slot to place pop-up tower")
				.defineInRange("slot_tower", -1, -1, 8);

		ninja_side_dist = BUILDER
				.comment("Ninja and god bridge's side distance from block center")
				.defineInRange("ninja_side_dist", 0.28, 0.0, 1.0);

		ninja_pitch = BUILDER
				.comment("Pitch of ninja bridge")
				.defineInRange("ninja_pitch", 77.5, -90.0, 90.0);

		ninja_walk_dist = BUILDER
				.comment("Ninja bridge's walk distance from base block's center. Higher value is faster but increases the chance of falling.")
				.defineInRange("ninja_walk_dist", 0.55, 0.5, 0.7);

		ninja_diag_pitch = BUILDER
				.comment("Pitch of ninja diagonal bridge")
				.defineInRange("ninja_diag_pitch", 76.0, -90.0, 90.0);

		ninja_diag_walk_dist = BUILDER
				.comment("Manhattan walk dist of ninja diagonal bridge. Higher value is faster but increases the chance of falling.")
				.defineInRange("ninja_diag_walk_dist", 1.1, 1.0, 1.5);

		ninja_wait_tick = BUILDER
				.comment("Ninja bridge ticks to wait after each step for 3 different cheat modes. Higher value is slower but decreases the chance of detected by the server.")
				.define("ninja_wait_tick", Arrays.asList(0, 1, 2));

		yaw_var = BUILDER
				.comment("Maximum variance of yaw when adjusting for 3 different cheat modes. Used for anti-anti-cheat.")
				.define("yaw_var", Arrays.asList(0.0, 0.1, 0.1));

		pitch_var = BUILDER
				.comment("Maximum variance of pitch when adjusting for 3 different cheat modes. Used for anti-anti-cheat. Higher values make you deviate from the lane.")
				.define("pitch_var", Arrays.asList(0.0, 0.1, 0.1));

		god_pitch = BUILDER
				.comment("Pitch of god bridge")
				.defineInRange("god_pitch", 76.0, -90.0, 90.0);

		god_walk_dist = BUILDER
				.comment("God bridge's walk distance from base block's center")
				.defineInRange("god_walk_dist", 0.5, -0.5, 1.0);

		color_start_bridge = BUILDER
				.comment("Hex color value for starting bridge prompt. Default: green")
				.define("color_start_bridge", "#00FF80");

		color_cancel_bridge = BUILDER
				.comment("Hex color value for canceling bridge prompt. Default: red")
				.define("color_cancel_bridge", "#FF0000");

		auto_login_command = BUILDER
				.comment("Auto login command, usually /login password (DO NOT include the slash)")
				.define("auto_login_command", "login password");

		cheat_mode = BUILDER
				.comment("Default cheat mode. 0: absolute  1: relative	2: slightly")
				.defineInRange("cheat_mode", 0, 0, 2);

		spam_left_min = BUILDER
				.comment("Spam left min interval, ms")
				.defineInRange("spam_left_min", 35.0, 1.0, 1000.0);

		spam_left_max = BUILDER
				.comment("Spam left max interval, ms")
				.defineInRange("spam_left_max", 50.0, 1.0, 1000.0);

		sort_delay = BUILDER
				.comment("Interval of sort, ms")
				.defineInRange("sort_delay", 500.0, 0.0, 1000.0);

		blockin_offset = BUILDER
				.comment("Block-in block placement offset from center (doubled). 1.0 means random on whole face, 0.0 means only center of face")
				.defineInRange("blockin_offset", 0.99, 0.0, 1.0);

		blockin_rotate_tick = BUILDER
				.comment("Block-in ticks for rotating")
				.defineInRange("blockin_rotate_tick", 2, 1, 20);

		delay_sword = BUILDER
				.comment("Delay after switching to sword, before spam-clicking, in ms")
				.defineInRange("delay_sword", 100, 0, 200);

		BUILDER.pop();
		CONFIG = BUILDER.build();
	}

	public static void loadConfig(ForgeConfigSpec config, Path path) {
		final CommentedFileConfig fileConfig = CommentedFileConfig.builder(path)
				.sync()
				.autosave()
				.writingMode(WritingMode.REPLACE)
				.build();

		fileConfig.load();
		config.setConfig(fileConfig);
	}

	@SubscribeEvent
	public static void onLoad(final FMLLoadCompleteEvent event) {
		loadConfig(CONFIG, FMLPaths.CONFIGDIR.get().resolve("winterbridge-config.toml"));
	}

	@SubscribeEvent
	public static void onCommonSetup(final FMLCommonSetupEvent event) {
		FMLJavaModLoadingContext.get().getModEventBus().register(ModConfig.class);
	}
	public static int getColorStartBridge(){
		try {
			return Integer.parseInt(color_start_bridge.get().substring(1, 7), 16);
		} catch (Exception ignored){ }
		return Integer.parseInt(color_start_bridge.getDefault().substring(1, 7), 16);
	}
	public static int getColorCancelBridge(){
		try {
			return Integer.parseInt(color_cancel_bridge.get().substring(1, 7), 16);
		} catch (Exception ignored){ }
		return Integer.parseInt(color_cancel_bridge.getDefault().substring(1, 7), 16);
	}
}
