package net.lzdq.winterbridge.client.handler.httpclass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CancelRequest {
    public String type;
    public CancelRequest(){
        this.type = "cancel";
    }
}
