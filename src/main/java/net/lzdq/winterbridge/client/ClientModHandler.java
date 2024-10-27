package net.lzdq.winterbridge.client;

import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.packet.PacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid= WinterBridge.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ClientModHandler {
	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event){
		for (KeyMapping key : ModKeyBindings.INSTANCE.keys.values())
			event.register(key);
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		PacketHandler.register();
	}

}
