package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class InfoRequest extends BaseInfoRequest{
    public long time;
    public Vec3 pos_delta;
    public InfoRequest(Minecraft mc){
        super(mc, "info", 11, 5, 11);
        this.time = System.currentTimeMillis();
        this.pos_delta = mc.player.getDeltaMovement();
    }
}
