package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class TestRequest extends BaseInfoRequest{
    public double sensitivity, xpos, ypos;
    public boolean smoothcamera;
    public int guiscalewidth, guiscaleheight, screenwidth, screenheight;
    public String[] player_names;
    public Vec3 pos_delta;
    public TestRequest(Minecraft mc){
        super(mc, "test", 5, 5, 5);
        this.sensitivity = mc.options.sensitivity().get();
        this.xpos = mc.mouseHandler.xpos();
        this.ypos = mc.mouseHandler.ypos();
        this.smoothcamera = mc.options.smoothCamera;
        this.guiscalewidth = mc.getWindow().getGuiScaledWidth();
        this.guiscaleheight = mc.getWindow().getGuiScaledHeight();
        this.screenwidth = mc.getWindow().getScreenWidth();
        this.screenheight = mc.getWindow().getScreenHeight();
        ClientPacketListener connection = mc.getConnection();

        if (connection != null) {
            Collection<PlayerInfo> playerInfo = connection.getOnlinePlayers();
            this.player_names = new String[playerInfo.size()];
            int i=0;
            for (PlayerInfo info: playerInfo) {
                this.player_names[i++] = info.getProfile().getName();
            }
            this.pos_delta = mc.player.getDeltaMovement();
        }
    }
}
