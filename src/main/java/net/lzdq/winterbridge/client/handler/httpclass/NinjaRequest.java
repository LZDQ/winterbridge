package net.lzdq.winterbridge.client.handler.httpclass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class NinjaRequest {
    public String type;
    public Vec2 dir;
    public Vec3 pos;
    public Vec3 eye;
    public String blocks;
    public NinjaRequest(Minecraft mc){
        LocalPlayer p = mc.player;
        this.type = "test";
        this.dir = p.getRotationVector();
        this.pos = p.position();
        this.eye = p.getEyePosition();
        BlockState blockState = mc.level.getBlockState(BlockPos.containing(pos.x, pos.y-1, pos.z));
        Block block = blockState.getBlock();
        this.blocks = block.getName().getString();
    }
}
