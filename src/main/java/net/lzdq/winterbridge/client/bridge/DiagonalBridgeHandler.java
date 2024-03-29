package net.lzdq.winterbridge.client.bridge;

import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.client.CheatMode;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public abstract class DiagonalBridgeHandler extends AbstractBridgeHandler{
    // Template for Diagonal bridge
    Direction dir_go_a, dir_go_d; // Direction of pressing a and d
    Vec3i vec_go;
    public DiagonalBridgeHandler(){
        super();
        float pitch = mc.player.getYRot();
        dir_go_a = Direction.fromYRot(pitch - 135);
        dir_go_d = Direction.fromYRot(pitch + 135);
        //mc.player.connection.sendChat("Directions: A " + dir_go_a.getName() + ", D " + dir_go_d.getName());
        vec_go = dir_go_a.getNormal().relative(dir_go_d);
        base_pos = mc.player.getOnPos();
        if (mc.player.getBlockStateOn().isAir()){
            if (!mc.level.getBlockState(base_pos.relative(dir_go_d.getOpposite())).isAir())
                base_pos = base_pos.relative((dir_go_d.getOpposite()));
            else if (!mc.level.getBlockState(base_pos.relative(dir_go_a.getOpposite())).isAir())
                base_pos = base_pos.relative((dir_go_a.getOpposite()));
            else
                base_pos = base_pos.relative((dir_go_d.getOpposite())).relative(dir_go_a.getOpposite());
        }
        //RotateHandler.init(new Vec2(ModConfig.ninja_yaw.get().floatValue(), dir_go.toYRot() - 135), 10);
        //WinterBridge.LOGGER.info("Starting bridge. Pitch: {} Direction: {}", pitch, dir_go.getName());
    }
    double getDistWalk(){
        // Directed dist from base_pos's center (exclude y)
        Vec3 center = base_pos.getCenter();
        return mc.player.position().subtract(center).dot(Vec3.atLowerCornerOf(vec_go));
    }
    void updateNextWalk(){
        left_tick = CheatMode.getNinjaWaitTick();
        last_y = base_pos.getY();
        if(left_up > 0 && --left_forward == 0){
            left_up--;
            left_forward = walk_forward;
            current_task = "walkup";
        } else current_task = "walk";
    }

    @Override
    void cancelTick(){
        KeyMapping.set(mc.options.keyShift.getKey(), cancel_cause.equals("manual"));
        KeyMapping.set(mc.options.keyDown.getKey(), false);
        KeyMapping.set(mc.options.keyJump.getKey(), false);
        current_task = "finish";
    }
}