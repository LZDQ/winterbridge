package net.lzdq.winterbridge.client.handler.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.Collection;

public class PlayerListRequest {
    public String type;
    public String[] names;

    public PlayerListRequest(Minecraft mc) {
        this.type = "playerlist";

        ClientPacketListener connection = mc.getConnection();

        if (connection != null) {
            Collection<PlayerInfo> playerInfo = connection.getOnlinePlayers();
            this.names = new String[playerInfo.size()];
            int i=0;
            for (PlayerInfo info: playerInfo) {
                this.names[i++] = info.getProfile().getName();
            }
        }
    }
}
