package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.client.action.PlaceBlockHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;


public class NinjaDiagonalHandler extends DiagonalBridgeHandler{
    int left_tick;
    public NinjaDiagonalHandler(String method){
        super();
        update(method);
        RotateHandler.init(new Vec2(
                        ModConfig.ninja_diag_yaw.get().floatValue(),
                        dir_go_d.toYRot() - 135.1F),
                10);
        left_tick = ModConfig.ninja_diag_wait_tick.get();
    }
    @Override
    public void update(String method){
        switch (method){
            case "ninja_diag":
                left_up = 0;
                walk_forward = 0;  // Doesn't matter
                left_forward = 0;  // Doesn't matter
                break;
            case "ninja_diag_inc":
                left_up = 100;
                walk_forward = 2;
                left_forward = 2;
                break;
        }
    }
    @Override
    void adjustTick(){
        mc.options.keyShift.setDown(true);
        mc.options.keyDown.setDown(true);
        boolean flag = true;
        if (!RotateHandler.finished())
            flag = false;
        /*
        mc.player.connection.sendChat("delta len sqr: " +
                mc.player.position().distanceToSqr(mc.player.xOld, mc.player.yOld, mc.player.zOld));
        //if (mc.player.getDeltaMovement().lengthSqr() > ModConfig.ninja_diag_thresh.get()) flag = false;
         */
        if (mc.player.position().distanceToSqr(mc.player.xOld, mc.player.yOld, mc.player.zOld)
                > ModConfig.ninja_diag_thresh.get())
            flag = false;
        if (flag && --left_tick < 0)
            current_task = "sneak";
    }
    @Override
    void walkTick(){
        if (getDistWalk() >= ModConfig.ninja_diag_walk_dist.get()){
            mc.options.keyShift.setDown(true);
            current_task = "sneak";
        }
    }
    @Override
    void sneakTick(){
        if (mc.player.getOnPos() != base_pos){
            if (mc.hitResult.getType() == HitResult.Type.BLOCK){
                BlockHitResult hit = (BlockHitResult) mc.hitResult;
                if (hit.getDirection() == dir_go_a || hit.getDirection() == dir_go_d){
                    //KeyMapping.click(mc.options.keyUse.getKey());
                    PlaceBlockHandler.placeBlock(hit);
                    //KeyMapping.click(mc.options.keyUse.getKey());
                } else if (!mc.player.getBlockStateOn().isAir()){
                    base_pos = base_pos.relative(dir_go_a).relative(dir_go_d);
                    mc.options.keyShift.setDown(false);
                    updateNextWalk();
                }
            }
        }
    }
}
