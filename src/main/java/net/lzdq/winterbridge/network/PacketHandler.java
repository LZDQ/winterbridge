package net.lzdq.winterbridge.network;

import net.lzdq.winterbridge.BridgeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation(BridgeMod.MODID, "main") )
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();
    public static void register(){
        INSTANCE.messageBuilder(SSpawnEntityPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SSpawnEntityPacket::encode)
                .decoder(SSpawnEntityPacket::new)
                .consumerMainThread(SSpawnEntityPacket::handle)
                .add();
    }
    public static void sendToServer(Object msg){
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }
}
