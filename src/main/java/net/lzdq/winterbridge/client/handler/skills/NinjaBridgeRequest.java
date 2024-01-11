package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;

public class NinjaBridgeRequest extends BaseInfoRequest{
    public NinjaBridgeRequest(Minecraft mc){
        super(mc, "ninjabridge", 3, 3, 3);
    }
}
