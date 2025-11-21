package net.lzdq.winterbridge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


@Mod.EventBusSubscriber(modid="winterbridge", bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec CONFIG;
	public static final ForgeConfigSpec.IntValue cheat_mode;
	public static final ForgeConfigSpec.DoubleValue ninja_side_dist, ninja_pitch, ninja_walk_dist;
	public static final ForgeConfigSpec.DoubleValue ninja_diag_pitch, ninja_diag_walk_dist;
	public static final ForgeConfigSpec.DoubleValue god_pitch, god_walk_dist;
	public static final ForgeConfigSpec.ConfigValue<List<Integer>> ninja_wait_tick;
	public static final ForgeConfigSpec.ConfigValue<List<Double>> yaw_var, pitch_var;
	public static final ForgeConfigSpec.ConfigValue<String> auto_login_command;
	public static final ForgeConfigSpec.IntValue spam_left_min, spam_left_max, timeout_doubleclick;
	public static final ForgeConfigSpec.IntValue blockin_rotate_tick;
	public static final ForgeConfigSpec.DoubleValue blockin_offset;
	public static final ForgeConfigSpec.IntValue delay_sword, delay_double_attack;
	public static final ForgeConfigSpec.DoubleValue spam_miss_click_prob;
	public static final ForgeConfigSpec.IntValue ladder_rotate_tick;

	static {
		BUILDER.push("Bridge Settings");

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

		BUILDER.pop();

		BUILDER.push("PVP Settings");

		spam_left_min = BUILDER
				.comment("Spam left min interval, ms")
				.defineInRange("spam_left_min", 35, 1, 1000);

		spam_left_max = BUILDER
				.comment("Spam left max interval, ms")
				.defineInRange("spam_left_max", 50, 1, 1000);

		delay_sword = BUILDER
				.comment("Delay after switching to sword, before spam-clicking, in ms")
				.defineInRange("delay_sword", 100, 0, 200);

		timeout_doubleclick = BUILDER
				.comment("Timeout for a double click")
				.defineInRange("timeout_doubleclick", 500, 0, 1000);

		delay_double_attack = BUILDER
				.comment("Delay between the two attacks, in ms")
				.defineInRange("delay_double_attack", 30, 0, 200);

		spam_miss_click_prob = BUILDER
				.comment("Probability of still left click if hitResult is not entity")
				.defineInRange("spam_miss_click_prob", 0.9, 0.0, 1.0);

		BUILDER.pop();

		BUILDER.push("Other Settings");

		blockin_offset = BUILDER
				.comment("Block-in block placement offset from center (doubled). 1.0 means random on whole face, 0.0 means only center of face")
				.defineInRange("blockin_offset", 0.99, 0.0, 1.0);

		blockin_rotate_tick = BUILDER
				.comment("Block-in ticks for rotating")
				.defineInRange("blockin_rotate_tick", 2, 1, 20);

		ladder_rotate_tick = BUILDER
				.comment("Block + ladder clutch rotate ticks (after block, before ladder). 0 means disabling rotate")
				.defineInRange("ladder_rotate_tick", 0, 0, 3);

		auto_login_command = BUILDER
				.comment("Auto login command, usually /login password (DO NOT include the slash)")
				.define("auto_login_command", "login password");

		cheat_mode = BUILDER
				.comment("Default cheat mode. 0: absolute  1: relative	2: slightly")
				.defineInRange("cheat_mode", 0, 0, 2);

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

	// @SubscribeEvent
	// public static void onLoad(final FMLLoadCompleteEvent event) {
	// 	loadConfig(CONFIG, FMLPaths.CONFIGDIR.get().resolve("winterbridge-config.toml"));
	// }

	// @SubscribeEvent
	// public static void onCommonSetup(final FMLCommonSetupEvent event) {
	// 	FMLJavaModLoadingContext.get().getModEventBus().register(ModConfig.class);
	// }
}
