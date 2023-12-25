package net.lzdq.winterbridge.client.handler.httpclass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class BlockInRequest {
    public String type;
    public Vec2 dir;
    public Vec3 pos;
    public Vec3 eye;
    public String[][][] blocks;
    public BlockInRequest(Minecraft mc){
        this.type = "blockin";
        LocalPlayer p = mc.player;
        this.dir= p.getRotationVector();
        this.pos= p.position();
        this.eye = p.getEyePosition();
        this.blocks = new String[11][21][11];
        for(int i=0; i<11; i++)
            for(int j=0; j<21; j++)
                for(int k=0; k<11; k++){
                    BlockState blockState = mc.level.getBlockState(BlockPos.containing(
                            this.pos.x + i - 5,
                            this.pos.y + j - 10,
                            this.pos.z + k - 5));
                    Block block = blockState.getBlock();
                    this.blocks[i][j][k] = block.getName().getString();
                }
    }
}
