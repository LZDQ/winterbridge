package net.lzdq.winterbridge.client.handler.httpclass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class InfoRequest {
    static int nx=3, ny=3, nz=3;
    public String type;
    public Vec2 dir;
    public Vec3 pos;
    public Vec3 eye;
    public String[][][] blocks;
    public InfoRequest(Minecraft mc){
        this.type = "info";
        LocalPlayer p = mc.player;
        this.dir= p.getRotationVector();
        this.pos= p.position();
        this.eye = p.getEyePosition();
        this.blocks = new String[nx][ny][nz];
        for(int i=0; i<nx; i++)
            for(int j=0; j<ny; j++)
                for(int k=0; k<nz; k++){
                    BlockState blockState = mc.level.getBlockState(BlockPos.containing(
                            this.pos.x + i - (nx >> 1),
                            this.pos.y + j - (ny >> 1),
                            this.pos.z + k - (nz >> 1)));
                    Block block = blockState.getBlock();
                    this.blocks[i][j][k] = block.getName().getString();
                }
    }
}
