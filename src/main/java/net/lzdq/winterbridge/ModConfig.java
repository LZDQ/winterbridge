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


@Mod.EventBusSubscriber(modid="winterbridge", bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;
    public static final ForgeConfigSpec.IntValue slot_sword, slot_block, slot_shear, slot_pickaxe, slot_axe;
    public static final ForgeConfigSpec.IntValue ninja_wait_tick, ninja_diag_wait_tick;
    public static final ForgeConfigSpec.DoubleValue ninja_side_dist, ninja_yaw, ninja_walk_dist;
    public static final ForgeConfigSpec.DoubleValue ninja_diag_yaw, ninja_diag_thresh, ninja_diag_walk_dist;
    public static final ForgeConfigSpec.DoubleValue god_yaw, god_walk_dist;

    static {
        BUILDER.push("General Settings");

        slot_sword = BUILDER
                .comment("Slot to place the sword")
                .defineInRange("slot_sword", 0, 0, 8);

        slot_block = BUILDER
                .comment("Slot to place the blocks")
                .defineInRange("slot_block", 4, 0, 8);

        slot_shear = BUILDER
                .comment("Slot to place the shear")
                .defineInRange("slot_shear", 2, 0, 8);

        slot_pickaxe = BUILDER
                .comment("Slot to place the pickaxe")
                .defineInRange("slot_pickaxe", 1, 0, 8);

        slot_axe = BUILDER
                .comment("Slot to place the axe")
                .defineInRange("slot_axe", 3, 0, 8);

        ninja_side_dist = BUILDER
                .comment("Ninja and god bridge's side distance from block center")
                .defineInRange("ninja_side_dist", 0.28, 0.0, 1.0);

        ninja_yaw = BUILDER
                .comment("Yaw of ninja bridge")
                .defineInRange("ninja_yaw", 78.0, -90.0, 90.0);

        ninja_walk_dist = BUILDER
                .comment("Ninja bridge's walk distance from base block's center")
                .defineInRange("ninja_walk_dist", 0.55, -0.5, 1.0);

        ninja_wait_tick = BUILDER
                .comment("Wait ticks after adjusted")
                .defineInRange("ninja_wait_tick", 0, 0, 20);

        ninja_diag_yaw = BUILDER
                .comment("Yaw of ninja diagonal bridge")
                .defineInRange("ninja_diag_yaw", 76.0, -90.0, 90.0);

        ninja_diag_wait_tick = BUILDER
                .comment("Wait ticks after adjusted diagonal")
                .defineInRange("ninja_diag_wait_tick", 2, 1, 20);

        ninja_diag_thresh = BUILDER
                .comment("Threshold for adjusting diagonal bridge")
                .defineInRange("ninja_diag_thresh", 1e-7, 0.0, 1.0);

        ninja_diag_walk_dist = BUILDER
                .comment("Manhattan walk dist of ninja diagonal bridge")
                .defineInRange("ninja_diag_walk_dist", 1.1, -1.0, 2.0);

        god_yaw = BUILDER
                .comment("Yaw of god bridge")
                .defineInRange("god_yaw", 76.0, -90.0, 90.0);

        god_walk_dist = BUILDER
                .comment("God bridge's walk distance from base block's center")
                .defineInRange("god_walk_dist", 0.5, -0.5, 1.0);

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
}
