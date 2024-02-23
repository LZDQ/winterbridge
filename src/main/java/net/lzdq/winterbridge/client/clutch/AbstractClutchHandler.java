package net.lzdq.winterbridge.client.clutch;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;

public abstract class AbstractClutchHandler {
    Minecraft mc;
    BlockPos base_pos;
    BlockHitResult hit;
    Vec2 oldRot;
    public AbstractClutchHandler(){
        mc = Minecraft.getInstance();
        oldRot = mc.player.getRotationVector();
    }
    public abstract void tick();
    public abstract boolean isFinished();
}
