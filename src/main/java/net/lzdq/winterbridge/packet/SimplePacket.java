package net.lzdq.winterbridge.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SimplePacket {
    public SimplePacket() {
    }

    public static void encode(SimplePacket msg, FriendlyByteBuf buffer) {
        // Nothing to encode for this simple packet
    }

    public static SimplePacket decode(FriendlyByteBuf buffer) {
        return new SimplePacket(); // No data carried by this packet
    }

    public static void handle(SimplePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender(); // Get the player who sent the packet
            if (player != null) {
                player.teleportRelative(0, 2, 0);
            }
        });
        context.setPacketHandled(true);
    }
}
