package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.client.Minecraft;

public abstract class AbstractBridgeHandler {
    Minecraft mc;
    String current_task, old_task;
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
    public void update(String method){ }
    public boolean setCancelled(){
        // set cancelled and return whether cancelled
        if (current_task.equals("finish"))
            return true;
        if (!current_task.equals("cancel")){
            old_task = current_task;
            current_task = "cancel";
        }
        return false;
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
    }
}
