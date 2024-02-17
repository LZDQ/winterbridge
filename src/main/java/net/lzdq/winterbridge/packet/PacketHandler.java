package net.lzdq.winterbridge.packet;

import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static int count_packets = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WinterBridge.MODID, "main"),
            () -> PROTOCOL_VERSION,
            (version) -> true,
            (version) -> true
    );
    public static void register() {
        INSTANCE.registerMessage(0, SimplePacket.class, SimplePacket::encode, SimplePacket::decode, SimplePacket::handle);
    }
}
