package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.WinterBridge;
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
    void walkupTick(){ }
    void sneakTick(){ }
    abstract void cancelTick();
    public boolean isFinished(){
        return current_task.equals("finish");
    }
    public void update(String method){ }
    public void setCancelled(String cause){
        mc.player.displayClientMessage(
                Component.literal("Cancel bridge. Cause: " + cause)
                        .withStyle(Style.EMPTY.withColor(ModConfig.getColorCancelBridge())),
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
}
