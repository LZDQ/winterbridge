package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;

public class DiagonalRequest extends BaseInfoRequest{
    public DiagonalRequest(Minecraft mc){
        super(mc, "diagonal", 3, 3, 3);
    }
}
