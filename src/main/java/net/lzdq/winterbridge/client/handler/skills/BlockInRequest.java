package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;

public class BlockInRequest extends BaseInfoRequest{
    public BlockInRequest(Minecraft mc){
        super(mc, "blockin", 7, 15, 7);
    }
}
