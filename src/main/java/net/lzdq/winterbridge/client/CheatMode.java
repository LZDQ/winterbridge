package net.lzdq.winterbridge.client;

import net.lzdq.winterbridge.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Random;

public class CheatMode {
    public static int cheat_mode = 0;
    private static final String[] mode_names = {"absolute", "relative", "slightly"};
    private static final Random rand = new Random();
    /*
    0 - Absolute cheat
    1 - Relative cheat
    2 - Slightly cheat
     */
    public static double getRand(double a){
        // Returns a random double in [-a, a] using normal distribution
        double res = rand.nextGaussian(0, a / 3);
        res = Math.min(res, a);
        res = Math.max(res, -a);
        return res;
    }
    public static double getYawVar(){
        return getRand(ModConfig.yaw_var.get().get(cheat_mode));
    }
    public static double getPitchVar(){
        return getRand(ModConfig.pitch_var.get().get(cheat_mode));
    }
    public static int getNinjaWaitTick(){
        return ModConfig.ninja_wait_tick.get().get(cheat_mode);
    }
    public static void changeCheatMode(){
        changeCheatMode((cheat_mode + 1) % 3);
    }
    public static void changeCheatMode(int mode){
        cheat_mode = mode;
        Minecraft.getInstance().player.displayClientMessage(Component.literal(mode_names[cheat_mode]), true);
    }
}
