package net.lzdq.winterbridge.client.handler;

import com.google.gson.Gson;
import net.lzdq.winterbridge.BridgeMod;
import net.lzdq.winterbridge.client.ModKeyBindings;
import net.lzdq.winterbridge.network.PacketHandler;
import net.lzdq.winterbridge.network.SSpawnEntityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


class SortRequest{
    public String type;
    public String[] hotbar;
    public SortRequest(){
        this.type = "sort";
        this.hotbar = new String[9];
    }
}
@Mod.EventBusSubscriber(modid=BridgeMod.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientForgeHandler {
    Minecraft minecraft = Minecraft.getInstance();
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        Minecraft minecraft = Minecraft.getInstance();
        if(ModKeyBindings.INSTANCE.KEY_SORT.consumeClick()){
            assert minecraft.player != null;
            minecraft.player.displayClientMessage(Component.literal("Sort"), true);
            Inventory inventory = minecraft.player.getInventory();
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
            SortRequest req = new SortRequest();
            for(int i=0; i<9; i++){
                ItemStack item = inventory.getItem(i);
                req.hotbar[i] = item.getDisplayName().getString();
            }

            Gson gson = new Gson();
            String jsonContent = gson.toJson(req);
            BridgeMod.LOGGER.info(jsonContent);

            try{
                String url = "http://127.0.0.1:1234/";
                //BridgeMod.LOGGER.info("Line 1");
                HttpClient client = HttpClient.newHttpClient();
                //BridgeMod.LOGGER.info("Line 2");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonContent))
                        .build();
                //BridgeMod.LOGGER.info("Line 3");
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                //BridgeMod.LOGGER.info("Line 4");
                // Print response
                //System.out.println("Response status code: " + response.statusCode());
                //System.out.println("Response body: " + response.body());
                //BridgeMod.LOGGER.info(String.format("%d", response.statusCode()));
                //BridgeMod.LOGGER.info(response.body());


                /*
                URL url = new URL("http://127.0.0.1:8080/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                int responseCode = con.getResponseCode();
                System.out.println("Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        // Print the response
                        System.out.println(response.toString());
                    }
                } else {
                    System.out.println("GET request not worked");
                }

                // Disconnect the connection
                con.disconnect();
                 */
            } catch (Exception e) {
                BridgeMod.LOGGER.info("Exception");
                e.printStackTrace();
            }

        }
        if(ModKeyBindings.INSTANCE.KEY_TEST.consumeClick()){
            assert minecraft.player != null;
            minecraft.player.displayClientMessage(Component.literal("Test"), true);
            //  PacketHandler.sendToServer(new SSpawnEntityPacket());  // Spawn random entity
            Screen screen = minecraft.screen;
            if(screen==null) minecraft.player.displayClientMessage(Component.literal("Fuck"), true);
            else minecraft.player.displayClientMessage(Component.literal("Height: " + screen.height + "  \nWidth: " + screen.width), false);
        }
    }
}
