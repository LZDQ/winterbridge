package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.client.CheatMode;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;


public class NinjaBridgeHandler extends OrthogonalBridgeHandler{
    public NinjaBridgeHandler(String method){
        super();
        update(method);
        RotateHandler.init(new Vec2(
                (float) (ModConfig.ninja_yaw.get().floatValue() + CheatMode.getYawVar()),
                (float) (dir_go.toYRot() - 135 + CheatMode.getPitchVar())), 10);
        left_tick = 2;
    }
    @Override
    public void update(String method){
        switch (method){
            case "ninja":
                left_up = 0;
                walk_forward = 0;  // Doesn't matter
                left_forward = 0;  // Doesn't matter
                break;
            case "ninja_inc":
                left_up = 5;
                walk_forward = 3;
                left_forward = 2;
                break;
        }
    }
    @Override
    void adjustTick(){
        mc.options.keyShift.setDown(true);
        double dist = getDistS() - ModConfig.ninja_side_dist.get();
        mc.options.keyDown.setDown(dist < 0.1);
        mc.options.keyRight.setDown(dist > -0.1);
        boolean flag = true;
        if (Math.abs(dist) > 0.1)
            flag = false;
        if (!mc.player.getBlockStateOn().isAir())
            flag = false;
        if (!RotateHandler.finished())
            flag = false;
        //mc.player.connection.sendChat("Left tick: " + left_tick);
        if (flag && --left_tick < 0)
            current_task = "sneak";
    }
    @Override
    void walkTick(){
        if (getDistWalk() >= ModConfig.ninja_walk_dist.get()){
            mc.options.keyShift.setDown(true);
            current_task = "sneak";
        }
    }
    @Override
    void sneakTick(){
        if (!mc.player.getOnPos().equals(base_pos)){
            if (mc.hitResult.getType() == HitResult.Type.BLOCK){
                BlockHitResult hit = (BlockHitResult) mc.hitResult;
                if (hit.getDirection().equals(dir_go) && --left_tick < 0){
                    ActionHandler.placeBlock(hit);
                } else if (!mc.player.getBlockStateOn().isAir()){
                    base_pos = base_pos.relative(dir_go);
                    mc.options.keyShift.setDown(false);
                    updateNextWalk();
                    if (!mc.level.getBlockState(base_pos.relative(dir_go)).isAir()){
                        setCancelled("reached");
                    }
                }
            }
        }
    }
}
