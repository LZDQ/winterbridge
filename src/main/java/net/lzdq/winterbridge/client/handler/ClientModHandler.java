package net.lzdq.winterbridge.client.handler;

import net.lzdq.winterbridge.BridgeMod;
import net.lzdq.winterbridge.client.ModKeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=BridgeMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ClientModHandler {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(ModKeyBindings.INSTANCE.KEY_SORT);
        event.register(ModKeyBindings.INSTANCE.KEY_TEST);
    }

}
