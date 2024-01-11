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
        event.register(ModKeyBindings.INSTANCE.KEY_RECORD);
        event.register(ModKeyBindings.INSTANCE.KEY_CANCEL);
        event.register(ModKeyBindings.INSTANCE.KEY_BLOCKIN);
        event.register(ModKeyBindings.INSTANCE.KEY_PLAYERS);
        event.register(ModKeyBindings.INSTANCE.KEY_NINJA);
        event.register(ModKeyBindings.INSTANCE.KEY_INC3);
        event.register(ModKeyBindings.INSTANCE.KEY_INC2);
        event.register(ModKeyBindings.INSTANCE.KEY_DIAGONAL);
        event.register(ModKeyBindings.INSTANCE.KEY_DIAGINC);
        event.register(ModKeyBindings.INSTANCE.KEY_TELLY);
        event.register(ModKeyBindings.INSTANCE.KEY_GOD);
    }

}
