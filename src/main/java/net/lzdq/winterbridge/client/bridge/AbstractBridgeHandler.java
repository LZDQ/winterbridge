package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.client.CheatMode;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public abstract class AbstractBridgeHandler {
    Minecraft mc;
    BlockPos base_pos;
    String current_task, old_task, cancel_cause;
    int walk_forward, left_forward, left_up, last_y, left_tick;
    public AbstractBridgeHandler(){
        this.mc = Minecraft.getInstance();
        this.current_task = "adjust";
    }
    /*
    current_task has several options:
    "adjust" - rotate to a direction and adjust position
    "walk" - walking
    "walkup" - walk and jump and place a block
    "sneak" - sneaking to the edge
    "cancel" - cancelling
    "finish" - finished cancelling
     */
    abstract void adjustTick();
    abstract void walkTick();
    void sneakTick(){ }
    abstract void cancelTick();
    public boolean isFinished(){
        return current_task.equals("finish");
    }
    public void update(String method){ }
    public void setCancelled(String cause){
        mc.player.displayClientMessage(
                Component.literal("Cancel bridge. Cause: " + cause)
                        .withStyle(Style.EMPTY.withColor(0xFF0000)),
                true
        );
        cancel_cause = cause;
        if (current_task.equals("finish"))
            return ;
        if (!current_task.equals("cancel")){
            old_task = current_task;
            current_task = "cancel";
        }
    }
    public void tick(){
        //mc.player.displayClientMessage(Component.literal(current_task), false);
        switch (current_task){
            case "adjust":
                adjustTick();
                break;
            case "walk":
                walkTick();
                break;
            case "walkup":
                walkupTick();
                break;
            case "sneak":
                sneakTick();
                break;
            case "cancel":
                cancelTick();
                break;
            case "finish":
                break;
            default:
                WinterBridge.LOGGER.error("Undefined current_task {}", current_task);
        }
        if (mc.player.getBlockY() <= base_pos.getY())
            setCancelled("fell");
    }

    void walkupTick(){
        mc.options.keyShift.setDown(CheatMode.cheat_mode > 0 || walk_forward == 1);  // Sneak in Relative and Slightly mode
        //mc.player.displayClientMessage(Component.literal("1"), false);
        if (base_pos.getY() == last_y) {
            //mc.player.displayClientMessage(Component.literal("2"), false);
            //WinterBridge.LOGGER.info("BlockY {}", mc.player.getBlockY());
            mc.options.keyJump.setDown(mc.player.getOnPos().getY() == last_y);
            if (mc.level.getBlockState(base_pos.above()).isAir()){
                if (mc.player.getY() >= last_y + 2.0) {
                    ActionHandler.placeBlock();
                }
            } else {
                base_pos = base_pos.above();
                if (CheatMode.cheat_mode > 0) {
                    current_task = "sneak";
                    left_tick = 0;
                } else {
                    current_task = "walk";
                }
            }
        } else {
            WinterBridge.LOGGER.warn("Warn! Still walkup");
        }
    }
}
