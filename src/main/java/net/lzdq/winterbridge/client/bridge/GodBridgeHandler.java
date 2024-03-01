package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class GodBridgeHandler extends OrthogonalBridgeHandler{
    public GodBridgeHandler(){
        super();
        left_up = 100;
        walk_forward = 8;
        left_forward = 8;
        RotateHandler.init(new Vec2(ModConfig.god_yaw.get().floatValue(), dir_go.toYRot() - 135), 10);
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
        if (!RotateHandler.finished())
            flag = false;
        if (flag){
            KeyMapping.click(mc.options.keyUse.getKey());
            mc.options.keyShift.setDown(false);
            current_task = "walk";
        }
    }
    @Override
    void walkTick(){
        if (getDistWalk() >= ModConfig.god_walk_dist.get()){
            if (mc.player.getBlockStateOn().isAir()){
                //KeyMapping.click(mc.options.keyUse.getKey());
                //mc.player.setPos(mc.player.getX(), Mth.ceil(mc.player.getY()), mc.player.getZ());
                if (mc.hitResult.getType() == HitResult.Type.BLOCK){
                    BlockHitResult hit = (BlockHitResult) mc.hitResult;
                    if (hit.getDirection() == Direction.UP){
                        // modify the packet
                        Vec3 loc = base_pos.getCenter().add(Vec3.atCenterOf(dir_go.getNormal()).scale(0.5));
                        loc = loc.add(0, 0.499, 0);
                        hit = new BlockHitResult(loc, dir_go, base_pos, false);
                    }
                    ActionHandler.placeBlock(hit);
                }
            } else {
                base_pos = base_pos.relative(dir_go);
                updateNextWalk();
            }
        }
    }
}
