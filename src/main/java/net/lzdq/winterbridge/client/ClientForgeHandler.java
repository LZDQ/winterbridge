package net.lzdq.winterbridge.client;

import io.netty.channel.*;
import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.client.bridge.*;
import net.lzdq.winterbridge.client.clutch.AbstractClutchHandler;
import net.lzdq.winterbridge.client.clutch.BlockClutchHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.lzdq.winterbridge.packet.PacketInspector;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid=WinterBridge.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientForgeHandler {
    static Minecraft mc;
    static boolean is_sorting = false;
    static boolean cancelled = false;
    static int spam_right_mode = 0;  // 0 - Not down   1 - Down but not click   2 - Down and click
    static long until = 0;
    static AbstractBridgeHandler bridgeHandler;
    static AbstractClutchHandler clutchHandler;
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END) return ;
        mc = Minecraft.getInstance();
        PacketInspector.connected = (mc.player != null);
        if (PacketInspector.connected != PacketInspector.modified){
            PacketInspector.modified = PacketInspector.connected;
            if (PacketInspector.connected){
                is_sorting = false;
                spam_right_mode = 0;
                bridgeHandler = null;
                clutchHandler = null;
                ChannelPipeline pipeline = mc.getConnection().getConnection().channel().pipeline();
                pipeline.addBefore("packet_handler", "my_packet_handler", new PacketInspector());
                WinterBridge.LOGGER.info(pipeline.names().toString());
            }
        }

        if (!PacketInspector.connected)
            return ;

        if (ModKeyBindings.INSTANCE.KEY_NINJA.consumeClick())
            startBridge("ninja");

        if (ModKeyBindings.INSTANCE.KEY_NINJA_INC.consumeClick())
            startBridge("ninja_inc");

        if (ModKeyBindings.INSTANCE.KEY_NINJA_DIAG.consumeClick())
            startBridge("ninja_diag");

        if (ModKeyBindings.INSTANCE.KEY_NINJA_DIAG_INC.consumeClick())
            startBridge("ninja_diag_inc");

        if (ModKeyBindings.INSTANCE.KEY_GOD.consumeClick())
            startBridge("god");

        if (ModKeyBindings.INSTANCE.KEY_SORT.consumeClick())
            is_sorting = true;

        //handleSpamClick();

        if (ModKeyBindings.INSTANCE.KEY_CANCEL.consumeClick())
            cancelled = true;

        if (mc.player.getMainHandItem().getItem() instanceof BlockItem &&
                mc.player.getBlockStateOn().isAir() &&
                mc.options.keyAttack.consumeClick()){
            // Holding block and clicking in the air, activate block clutch
            if (clutchHandler == null)
                clutchHandler = new BlockClutchHandler();
        }

        if (System.currentTimeMillis() < until)
            return ;

        if (is_sorting)
            sortItems();

        if (bridgeHandler != null) {
            bridgeHandler.tick();
            if (cancelled) {
                if (bridgeHandler.setCancelled())
                    bridgeHandler = null;
            }
        }

        if (clutchHandler != null){
            if (clutchHandler.finished())
                clutchHandler = null;
            else clutchHandler.tick();
        }
        RotateHandler.tick();
    }
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event){
        if (event.phase == TickEvent.Phase.END)
            return ;
        handleSpamClick();
    }
    private static boolean doSwap(int slot, int dest){
        double interval = 0.5;
        if (slot == -1 || slot == dest + InventoryMenu.USE_ROW_SLOT_START)
            return false;
        swapSlot(slot, dest);
        until = (long) (System.currentTimeMillis() + 1000 * interval);
        return true;
    }
    private static void sortItems(){
        Minecraft mc = Minecraft.getInstance();
        InventoryMenu inv_menu = mc.player.inventoryMenu;
        List<ItemStack> items = inv_menu.getItems();

        int slot;
        // Sword
        Item[] sword_items = {
                Items.DIAMOND_SWORD,
                Items.IRON_SWORD,
                Items.STONE_SWORD,
                Items.WOODEN_SWORD
        };
        slot = -1;
        for (Item sword : sword_items){
            for (int i = 0; i < items.size(); i++)
                if (items.get(i).is(sword)){
                    slot= i;
                    break;
                }
            if (slot != -1)
                break;
        }
        // slot_sword will be [0, items.size()) if it exists and -1 otherwise
        if (doSwap(slot, ModConfig.slot_sword.get())) return ;

        // Block
        int max_count = -1;
        slot = -1;
        for (int i = 0; i < items.size(); i++){
            ItemStack item = items.get(i);
            if (item.getItem() instanceof BlockItem && item.getCount() > max_count){
                slot = i;
                max_count = item.getCount();
            }
        }
        if (doSwap(slot, ModConfig.slot_block.get())) return ;

        // Shear
        slot = -1;
        for (int i = 0; i < items.size(); i++){
            ItemStack item = items.get(i);
            if (item.is(Items.SHEARS)){
                slot = i;
                break;
            }
        }
        if (doSwap(slot, ModConfig.slot_shear.get())) return ;

        // Pickaxe
        Item[] pickaxe_items = {
                Items.DIAMOND_PICKAXE,
                Items.GOLDEN_PICKAXE,
                Items.IRON_PICKAXE,
                Items.WOODEN_PICKAXE
        };
        slot = -1;
        for (Item pickaxe : pickaxe_items){
            for (int i = 0; i < items.size(); i++){
                ItemStack item = items.get(i);
                if (item.is(pickaxe)){
                    slot = i;
                    break;
                }
            }
            if (slot != -1)
                break;
        }
        if (doSwap(slot, ModConfig.slot_pickaxe.get())) return ;

        // Pickaxe
        Item[] axe_items = {
                Items.DIAMOND_AXE,
                Items.IRON_AXE,
                Items.STONE_AXE,
                Items.WOODEN_AXE
        };
        slot = -1;
        for (Item axe : axe_items){
            for (int i = 0; i < items.size(); i++){
                ItemStack item = items.get(i);
                if (item.is(axe)){
                    slot = i;
                    break;
                }
            }
            if (slot != -1)
                break;
        }
        if (doSwap(slot, ModConfig.slot_axe.get())) return ;

        is_sorting = false;
    }
    private static void swapSlot(int slot_from, int slot_to){
        // slot_from is the inventory id (for hotbar, it is [36, 45) )
        // slot_to is the hotbar id [0, 9)
        Minecraft mc = Minecraft.getInstance();
        //mc.player.sendOpenInventory();
        //Inventory inventory = mc.player.getInventory();
        InventoryMenu inv_menu = mc.player.inventoryMenu;
        mc.gameMode.handleInventoryMouseClick(
                inv_menu.containerId,
                slot_from,
                slot_to,
                ClickType.SWAP,
                mc.player
        );
    }
    private static void handleSpamClick(){
        if (mc.options.keyHotbarSlots[ModConfig.slot_sword.get()].isDown()){
            // Spam-click when holding switching to sword
            KeyMapping.click(mc.options.keyAttack.getKey());
        }
        if (mc.options.keyHotbarSlots[ModConfig.slot_block.get()].isDown()){
            if (spam_right_mode == 0){
                if (mc.player.getInventory().selected == ModConfig.slot_block.get()){
                    // Already holding block. Start spam-click
                    spam_right_mode = 2;
                } else {
                    // Not holding, don't click
                    spam_right_mode = 1;
                }
            }
            if (spam_right_mode == 2)
                KeyMapping.click(mc.options.keyUse.getKey());
        } else spam_right_mode = 0;
    }
    private static void startBridge(String method){
        cancelled = false;
        if (bridgeHandler == null){
            if (method.startsWith("ninja") && !method.startsWith("ninja_diag"))
                bridgeHandler = new NinjaBridgeHandler(method);
            else if (method.startsWith("ninja_diag"))
                bridgeHandler = new NinjaDiagonalHandler(method);
            else if (method.equals("god"))
                bridgeHandler = new GodBridgeHandler();
        } else {
            bridgeHandler.update(method);
        }
    }
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event){
        //BridgeMod.LOGGER.info(event.getEntity().getName().getString() + " has been hurt!");
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(player == mc.player){
                // Hit
                // AutoCancel
            }
        }
    }
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event){
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        WinterBridge.LOGGER.info("Player login");
    }

    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event){
        //WinterBridge.LOGGER.info("interaction triggered");
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton event){
        //WinterBridge.LOGGER.info("Mouse Input: button {} action {}", event.getButton(), event.getAction());
    }
}

