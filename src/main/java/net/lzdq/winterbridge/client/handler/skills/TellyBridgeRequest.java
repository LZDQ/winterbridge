package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;

public class TellyBridgeRequest extends BaseInfoRequest{
    public TellyBridgeRequest(Minecraft mc){
        super(mc, "tellybridge", 11, 5, 11);
    }
}
