package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.client.CheatMode;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;


public class NinjaDiagonalHandler extends DiagonalBridgeHandler{
    Direction last_hit_dir = null;
    public NinjaDiagonalHandler(String method){
        super();
        update(method);
        RotateHandler.init(new Vec2(
                        (float) (ModConfig.ninja_diag_yaw.get() + CheatMode.getYawVar()),
                        (float) (dir_go_d.toYRot() - 135 + 0.5 + Math.abs(CheatMode.getPitchVar()))),
                10);
        left_tick = 2;
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
                > 1e-5)
            flag = false;
        if (flag && --left_tick < 0)
            current_task = "sneak";
    }
    @Override
    void walkTick(){
        if (getDistWalk() >= ModConfig.ninja_diag_walk_dist.get()){
            mc.options.keyShift.setDown(true);
            current_task = "sneak";
        } else {
            mc.options.keyShift.setDown(false);
        }
    }
    @Override
    void sneakTick(){
        if (!mc.player.getOnPos().equals(base_pos)){
            //mc.player.displayClientMessage(Component.literal("on edge"), false);
            if (mc.hitResult.getType() == HitResult.Type.BLOCK){
                BlockHitResult hit = (BlockHitResult) mc.hitResult;
                if (hit.getDirection() != Direction.UP &&
                        (hit.getDirection() != last_hit_dir ||
                                mc.player.position().distanceToSqr(mc.player.xOld, mc.player.yOld, mc.player.zOld)
                                        < 1e-5)){
                    //mc.player.displayClientMessage(Component.literal("2"), false);
                    //KeyMapping.click(mc.options.keyUse.getKey());
                    if (--left_tick < 0) {
                        ActionHandler.placeBlock(hit);
                        last_hit_dir = hit.getDirection();
                    }
                    //KeyMapping.click(mc.options.keyUse.getKey());
                } else if (!mc.player.getBlockStateOn().isAir()){
                    //mc.player.displayClientMessage(Component.literal("3"), false);
                    //mc.player.displayClientMessage(Component.literal(base_pos.toShortString()), false);
                    //mc.player.displayClientMessage(Component.literal(mc.player.getOnPos().toShortString()), false);
                    base_pos = base_pos.relative(dir_go_a).relative(dir_go_d);
                    //mc.options.keyShift.setDown(false);
                    updateNextWalk();
                    if (!mc.level.getBlockState(base_pos.above().relative(dir_go_a)).isAir() ||
                            !mc.level.getBlockState(base_pos.above().relative(dir_go_d)).isAir()){
                        setCancelled("reached");
                    }
                }
            }
        }
    }
}
