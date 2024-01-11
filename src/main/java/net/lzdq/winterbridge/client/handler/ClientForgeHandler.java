package net.lzdq.winterbridge.client.handler;

import net.lzdq.winterbridge.BridgeMod;
import net.lzdq.winterbridge.client.ModKeyBindings;
import net.lzdq.winterbridge.client.handler.skills.*;
import net.lzdq.winterbridge.communicate.ToPython;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid=BridgeMod.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientForgeHandler {
    static boolean enable_info=false;
    static long t1=0, poll=20;
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        Minecraft mc = Minecraft.getInstance();

        if(ModKeyBindings.INSTANCE.KEY_SORT.consumeClick()){
            assert mc.player != null;
            mc.player.displayClientMessage(Component.literal("Sort"), true);
            //for(int i=0; i<9; i++) inventory.items.set(i, new ItemStack(Items.DIAMOND, 10));
            /*
            int slot_weapon = -1, slot_pickaxe = -1, slot_shear = -1, slot_wool = -1, block_count = 0;
            for(int i=0; i<9; i++){
                Item item= inventory.getItem(i).getItem();
                if(item instanceof SwordItem)
                    slot_weapon = i;
                else if(item instanceof PickaxeItem)
                    slot_pickaxe = i;
                else if(item instanceof ShearsItem)
                    slot_shear = i;
                else if(item instanceof BlockItem && inventory.getItem(i).getCount() > block_count){
                    slot_wool = i;
                    block_count = inventory.getItem(i).getCount();
                }
            }
            //if(slot_weapon!=-1) inventory.setItem(slot_weapon, new ItemStack(Items.DIAMOND_SWORD));
             */

            ToPython.send(new SortRequest(mc));

        }

        if(ModKeyBindings.INSTANCE.KEY_BLOCKIN.consumeClick()){
            ToPython.send(new BlockInRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_PLAYERS.consumeClick()){
            ToPython.send(new PlayerListRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_NINJA.consumeClick()){
            enable_info = true;
            ToPython.send(new NinjaBridgeRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_INC3.consumeClick()){
            enable_info = true;
            ToPython.send(new NinjaInc3Request(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_INC2.consumeClick()){
            enable_info = true;
            ToPython.send(new NinjaInc2Request(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_TELLY.consumeClick()){
            enable_info = true;
            ToPython.send(new TellyBridgeRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_GOD.consumeClick()){
            enable_info = true;
            ToPython.send(new GodBridgeRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_TEST.consumeClick()){
            enable_info = true;
            assert mc.player != null;
            mc.player.displayClientMessage(Component.literal("Test"), true);
            //  PacketHandler.sendToServer(new SSpawnEntityPacket());  // Spawn random entity
            ToPython.send(new TestRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_RECORD.consumeClick()){
            enable_info = true;
            ToPython.send(new RecordRequest(mc));
        }

        if(ModKeyBindings.INSTANCE.KEY_CANCEL.consumeClick()){
            enable_info = false;
            ToPython.send(new CancelRequest("manual"));
        }
        if(enable_info && event.phase == TickEvent.Phase.END){
            if(mc.player != null){
                if(!ToPython.send(new InfoRequest(mc)))
                    enable_info = false;
            }else{
                enable_info = false;
            }
        }
    }
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event){
        Minecraft mc = Minecraft.getInstance();
        //BridgeMod.LOGGER.info(event.getEntity().getName().getString() + " has been hurt!");
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(player == mc.player){
                // Hit
                // AutoCancel
                enable_info = false;
                ToPython.send(new CancelRequest("hit"));
            }
        }
    }
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event){
        if(true) return;
        long t2 = System.currentTimeMillis();
        if(enable_info && t2 - t1 >= poll){
            t1 = t2;
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null){
                ToPython.send(new InfoRequest(mc));
            }else{
                enable_info = false;
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(false && event.phase == TickEvent.Phase.END && enable_info){
            Minecraft mc = Minecraft.getInstance();
            ToPython.send(new InfoRequest(mc));
        }
    }
}

