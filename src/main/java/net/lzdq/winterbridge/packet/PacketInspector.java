package net.lzdq.winterbridge.packet;

import com.google.gson.Gson;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.network.protocol.game.*;

import java.util.Arrays;
import java.util.List;

public class PacketInspector extends ChannelDuplexHandler {
    public static boolean connected = false;
    public static boolean modified = false;
    public PacketInspector(){}
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        super.channelRead(ctx, msg);
        // Here are the server side packets received
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Here are the client side packets sent out
        super.write(ctx, msg, promise);
        List<Class<?>> ignores = Arrays.asList(
                ServerboundMovePlayerPacket.class,
                ServerboundMovePlayerPacket.Pos.class,
                ServerboundMovePlayerPacket.Rot.class,
                ServerboundMovePlayerPacket.PosRot.class,
                ServerboundSwingPacket.class,
                ServerboundKeepAlivePacket.class,
                ServerboundPongPacket.class
        );

        if (ignores.stream().anyMatch(clazz -> clazz.isInstance(msg)))
            return ;
        WinterBridge.LOGGER.debug(msg.getClass().getName());
        try {
            Gson gson = new Gson();
            WinterBridge.LOGGER.debug(gson.toJson(msg));
        } catch (Exception e){
            if (msg instanceof ServerboundContainerClickPacket){
                ServerboundContainerClickPacket packet = (ServerboundContainerClickPacket) msg;
                WinterBridge.LOGGER.debug("container id " + packet.getContainerId());
                WinterBridge.LOGGER.debug("slot num " + packet.getSlotNum());
                WinterBridge.LOGGER.debug("button num " + packet.getButtonNum());
                WinterBridge.LOGGER.debug("item stack " + packet.getCarriedItem().toString());
                WinterBridge.LOGGER.debug("changed slots " + packet.getChangedSlots().toString());
                WinterBridge.LOGGER.debug("click type " + packet.getClickType().toString());
                WinterBridge.LOGGER.debug("state id " + packet.getStateId());
            }
        }
    }
}
