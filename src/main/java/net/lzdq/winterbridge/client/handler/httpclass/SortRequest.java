package net.lzdq.winterbridge.client.handler.httpclass;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SortRequest {
    public String type;
    public String[] hotbar;

    public SortRequest(Minecraft mc) {
        this.type = "sort";
        this.hotbar = new String[9];
        Inventory inventory = mc.player.getInventory();
        for(int i=0; i<9; i++){
            ItemStack item = inventory.getItem(i);
            this.hotbar[i] = item.getDisplayName().getString();
        }
    }
}
