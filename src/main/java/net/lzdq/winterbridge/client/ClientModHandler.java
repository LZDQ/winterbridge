package net.lzdq.winterbridge.client;

import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.packet.PacketHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid= WinterBridge.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ClientModHandler {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(ModKeyBindings.INSTANCE.KEY_SORT);
        event.register(ModKeyBindings.INSTANCE.KEY_CANCEL);
        event.register(ModKeyBindings.INSTANCE.KEY_BLOCKS);
        event.register(ModKeyBindings.INSTANCE.KEY_BLOCKIN);
        event.register(ModKeyBindings.INSTANCE.KEY_NINJA);
        event.register(ModKeyBindings.INSTANCE.KEY_NINJA_INC1);
        event.register(ModKeyBindings.INSTANCE.KEY_NINJA_INC3);
        event.register(ModKeyBindings.INSTANCE.KEY_NINJA_DIAG);
        event.register(ModKeyBindings.INSTANCE.KEY_NINJA_DIAG_INC);
        event.register(ModKeyBindings.INSTANCE.KEY_CHEAT_MODE);
        event.register(ModKeyBindings.INSTANCE.KEY_GOD);
        event.register(ModKeyBindings.INSTANCE.KEY_FIREBALL);
        event.register(ModKeyBindings.INSTANCE.KEY_EPEARL);
        event.register(ModKeyBindings.INSTANCE.KEY_EGG);
        event.register(ModKeyBindings.INSTANCE.KEY_TOWER);
        event.register(ModKeyBindings.INSTANCE.KEY_GAPPLE);
        event.register(ModKeyBindings.INSTANCE.KEY_STICK);
        event.register(ModKeyBindings.INSTANCE.KEY_HARD_BLOCK);
        event.register(ModKeyBindings.INSTANCE.KEY_AUTO_TOOL);
        event.register(ModKeyBindings.INSTANCE.KEY_AUTO_LOGIN);
        event.register(ModKeyBindings.INSTANCE.KEY_AUTO_WHO);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

}
