package net.lzdq.winterbridge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WinterBridge.MODID)
public class WinterBridge {
    public static final String MODID = "winterbridge";
    public static final Logger LOGGER = LogUtils.getLogger();
    public WinterBridge(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        //modEventBus.addListener(this::addCreative);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
