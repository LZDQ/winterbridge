package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;

public class RecordRequest{
    public String type;
    public RecordRequest(Minecraft mc){
        this.type = "record";
    }
}
