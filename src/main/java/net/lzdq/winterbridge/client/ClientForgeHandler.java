package net.lzdq.winterbridge.client;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.lzdq.winterbridge.client.blockin.BlockInHandler;
import net.lzdq.winterbridge.client.bridge.*;
import net.lzdq.winterbridge.client.clutch.AbstractClutchHandler;
import net.lzdq.winterbridge.client.clutch.BlockClutchHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.lzdq.winterbridge.packet.PacketInspector;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
    static BlockInHandler blockinHandler;
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
                CheatMode.changeCheatMode(ModConfig.cheat_mode.get());
                /*
                ChannelPipeline pipeline = mc.getConnection().getConnection().channel().pipeline();
                pipeline.addBefore("packet_handler", "my_packet_handler", new PacketInspector());
                WinterBridge.LOGGER.info(pipeline.names().toString());
                 */
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

        if (ModKeyBindings.INSTANCE.KEY_SORT.consumeClick()) {
            is_sorting = true;
            cancelled = false;
        }

        handleSpamClickRight();

        if (ModKeyBindings.INSTANCE.KEY_CANCEL.consumeClick()) {
            cancelled = true;
            is_sorting = false;
            RotateHandler.setCancelled();
        }

        if (ModKeyBindings.INSTANCE.KEY_CHEAT_MODE.consumeClick())
            CheatMode.changeCheatMode();

        if (ModKeyBindings.INSTANCE.KEY_FIREBALL.consumeClick())
            switchToItem(Items.FIRE_CHARGE, ModConfig.use_fireball.get());

        if (ModKeyBindings.INSTANCE.KEY_EPEARL.consumeClick())
            switchToItem(Items.ENDER_PEARL, ModConfig.use_epearl.get());

        if (ModKeyBindings.INSTANCE.KEY_EGG.consumeClick())
            switchToItem(Items.EGG);

        if (ModKeyBindings.INSTANCE.KEY_TOWER.consumeClick())
            switchToItem(Items.CHEST);

        if (ModKeyBindings.INSTANCE.KEY_TOWER.consumeClick())
            switchToItem(Items.GOLDEN_APPLE);

        if (ModKeyBindings.INSTANCE.KEY_HARD_BLOCK.consumeClick()){
            Inventory inv = mc.player.getInventory();
            String[] blocks = {
                    "Obsidian",
                    "End Stone",
                    "Plank", "Log",
                    "Clay",
                    "Wool"
            };
            for (int i = 0; i < 9; i++){
                for (String s : blocks){
                    if (inv.getSelected().getDisplayName().getString().contains(s))
                        break;
                    if (inv.getItem(i).getDisplayName().getString().contains(s)){
                        inv.selected = i;
                        break;
                    }
                }
            }
            if (ModConfig.use_hard_block.get())
                ActionHandler.useItem();
        }

        if (ModKeyBindings.INSTANCE.KEY_AUTO_TOOL.consumeClick())
            autoSwitchTool();

        if (ModKeyBindings.INSTANCE.KEY_AUTO_LOGIN.consumeClick())
            mc.player.connection.sendCommand(ModConfig.auto_login_command.get());

        if (ModKeyBindings.INSTANCE.KEY_AUTO_WHO.consumeClick())
            mc.player.connection.sendCommand("who");

        if (ModKeyBindings.INSTANCE.KEY_BLOCKIN.consumeClick()){
            cancelled = false;
            if (blockinHandler == null)
                blockinHandler = new BlockInHandler();
        }

        if (CheatMode.cheat_mode < 2 &&
                mc.player.getMainHandItem().getItem() instanceof BlockItem &&
                mc.player.getBlockStateOn().isAir() &&
                mc.options.keyAttack.consumeClick()){
            // Holding block and clicking in the air, activate block clutch
            if (clutchHandler == null)
                clutchHandler = new BlockClutchHandler();
        }

        if (System.currentTimeMillis() < until)
            return ;

        if (is_sorting && !cancelled)
            sortItems();

        if (bridgeHandler != null) {
            bridgeHandler.tick();
            if (cancelled)
                bridgeHandler.setCancelled("manual");
            if (bridgeHandler.isFinished())
                bridgeHandler = null;
        }

        if (clutchHandler != null){
            if (clutchHandler.isFinished())
                clutchHandler = null;
            else clutchHandler.tick();
        }

        if (blockinHandler != null){
            if (blockinHandler.isFinished())
                blockinHandler = null;
            else if (cancelled)
                blockinHandler = null;
            else blockinHandler.tick();
        }

        RotateHandler.tick();
    }
    private static boolean switchToItem(Item item){
        Inventory inv = mc.player.getInventory();
        for (int i = 0; i < 9; i++)
            if (inv.getItem(i).is(item)){
                inv.selected = i;
                return true;
            }
        return false;
    }
    private static boolean switchToItem(Item item, boolean use){
        boolean res = switchToItem(item);
        if (res && use)
            ActionHandler.useItem();
        return res;
    }
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event){
        if (event.phase == TickEvent.Phase.END)
            return ;
        handleSpamClickLeft();
    }
    private static boolean doSwap(int slot, int dest){
        if (slot == -1 || slot == dest + InventoryMenu.USE_ROW_SLOT_START)
            return false;
        swapSlot(slot, dest);
        until = (long) (System.currentTimeMillis() + ModConfig.sort_delay.get() * (Math.random() * 0.3 + 0.7));
        return true;
    }
    private static void sortItems(){
        Minecraft mc = Minecraft.getInstance();
        InventoryMenu inv_menu = mc.player.inventoryMenu;
        List<ItemStack> items = inv_menu.getItems();

        int slot;
        // Sword
        if (ModConfig.slot_sword.get() != -1) {
            Item[] sword_items = {
                    Items.DIAMOND_SWORD,
                    Items.IRON_SWORD,
                    Items.STONE_SWORD,
                    Items.WOODEN_SWORD
            };
            slot = -1;
            for (Item sword : sword_items) {
                for (int i = 0; i < items.size(); i++)
                    if (items.get(i).is(sword)) {
                        slot = i;
                        break;
                    }
                if (slot != -1)
                    break;
            }
            // slot will be [0, items.size()) if it exists and -1 otherwise
            if (doSwap(slot, ModConfig.slot_sword.get())) return;
        }

        // Block
        if (ModConfig.slot_block.get() != -1) {
            int max_count = mc.player.getInventory().getSelected().getCount();
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack itemstack = items.get(i);
                if (itemstack.getItem() instanceof BlockItem && itemstack.getCount() > max_count) {
                    slot = i;
                    max_count = itemstack.getCount();
                }
            }
            if (doSwap(slot, ModConfig.slot_block.get())) return;
        }

        // Shear
        if (ModConfig.slot_shear.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.SHEARS)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_shear.get())) return;
        }

        // Pickaxe
        if (ModConfig.slot_pickaxe.get() != -1) {
            Item[] pickaxe_items = {
                    Items.DIAMOND_PICKAXE,
                    Items.GOLDEN_PICKAXE,
                    Items.IRON_PICKAXE,
                    Items.WOODEN_PICKAXE
            };
            slot = -1;
            for (Item pickaxe : pickaxe_items) {
                for (int i = 0; i < items.size(); i++) {
                    ItemStack item = items.get(i);
                    if (item.is(pickaxe)) {
                        slot = i;
                        break;
                    }
                }
                if (slot != -1)
                    break;
            }
            if (doSwap(slot, ModConfig.slot_pickaxe.get())) return;
        }

        // Axe
        if (ModConfig.slot_axe.get() != -1) {
            Item[] axe_items = {
                    Items.DIAMOND_AXE,
                    Items.IRON_AXE,
                    Items.STONE_AXE,
                    Items.WOODEN_AXE
            };
            slot = -1;
            for (Item axe : axe_items) {
                for (int i = 0; i < items.size(); i++) {
                    ItemStack item = items.get(i);
                    if (item.is(axe)) {
                        slot = i;
                        break;
                    }
                }
                if (slot != -1)
                    break;
            }
            if (doSwap(slot, ModConfig.slot_axe.get())) return;
        }

        // Gapple
        if (ModConfig.slot_gapple.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.GOLDEN_APPLE)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_gapple.get())) return;
        }

        // Fireball
        if (ModConfig.slot_fireball.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.FIRE_CHARGE)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_fireball.get())) return;
        }

        // TNT
        if (ModConfig.slot_tnt.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.TNT)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_tnt.get())) return;
        }

        // Ladder
        if (ModConfig.slot_ladder.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.LADDER)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_ladder.get())) return;
        }

        // Knock-back stick
        if (ModConfig.slot_kbstick.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.STICK)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_kbstick.get())) return;
        }

        // Bridge egg
        if (ModConfig.slot_egg.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.EGG)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_egg.get())) return;
        }

        // Pop-up tower
        if (ModConfig.slot_tower.get() != -1) {
            slot = -1;
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.is(Items.CHEST)) {
                    slot = i;
                    break;
                }
            }
            if (doSwap(slot, ModConfig.slot_tower.get())) return;
        }

        mc.player.closeContainer();
        mc.player.displayClientMessage(Component.literal("Sorted"), true);

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
    public static void autoSwitchTool(){
        if (mc.hitResult.getType() != HitResult.Type.BLOCK)
            return ;
        BlockHitResult hit = (BlockHitResult) mc.hitResult;
        BlockState blockState = mc.level.getBlockState(hit.getBlockPos());
        Inventory inv = mc.player.getInventory();
        int slot = inv.selected;
        for (int i = 0; i < 9; i++)
            if (inv.getItem(i).getDestroySpeed(blockState) > inv.getItem(slot).getDestroySpeed(blockState))
                slot = i;
        inv.selected = slot;
    }
    private static void handleSpamClickLeft() {
        if (System.currentTimeMillis() < until)
            return;
        if (mc.options.keyHotbarSlots[ModConfig.slot_sword.get()].isDown()) {
            mc.player.getInventory().selected = ModConfig.slot_sword.get();
            // Spam-click when holding switching to sword
            KeyMapping.click(mc.options.keyAttack.getKey());
            until = (long) (System.currentTimeMillis() +
                    ModConfig.spam_left_min.get() +
                    Math.random() * (ModConfig.spam_left_max.get() - ModConfig.spam_left_min.get()));
        }
    }
    private static void handleSpamClickRight(){
        // Every client tick
        if (ModKeyBindings.INSTANCE.KEY_BLOCKS.isDown()){
            Inventory inv = mc.player.getInventory();
            if (spam_right_mode == 0){
                if (inv.getSelected().getItem() instanceof BlockItem){
                    // Already holding block. Start spam-click
                    spam_right_mode = 2;
                } else {
                    // Not holding, switch to block
                    spam_right_mode = 1;
                    int slot = inv.selected, mxcnt = 0;
                    for (int i = 0; i < 9; i++)
                        if (inv.getItem(i).getItem() instanceof BlockItem &&
                                inv.getItem(i).getCount() > mxcnt){
                            slot = i;
                            mxcnt = inv.getItem(i).getCount();
                        }
                    inv.selected = slot;
                }
            }
            if (spam_right_mode == 2)
                KeyMapping.click(mc.options.keyUse.getKey());
        } else spam_right_mode = 0;
    }
    private static void startBridge(String method){
        mc.player.displayClientMessage(
                Component.literal("Start bridge: " + method)
                        .withStyle(Style.EMPTY.withColor(ModConfig.getColorStartBridge())),
                true
        );
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
                if (bridgeHandler != null)
                    bridgeHandler.setCancelled("hit");
                if (blockinHandler != null)
                    blockinHandler.setCancelled("hit");
            }
        }
    }
}

