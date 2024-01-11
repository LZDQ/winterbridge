package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;

public class GodBridgeRequest extends BaseInfoRequest{
    public GodBridgeRequest(Minecraft mc){
        super(mc, "godbridge", 3, 3, 3);
    }
}
