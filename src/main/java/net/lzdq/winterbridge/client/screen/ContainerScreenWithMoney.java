package net.lzdq.winterbridge.client.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.lzdq.winterbridge.WinterBridge;
import net.lzdq.winterbridge.client.ModKeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class ContainerScreenWithMoney extends ContainerScreen {
	public ContainerScreenWithMoney(ChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
	
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Implement your custom key handling logic here
        if(super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		// WinterBridge.LOGGER.debug("fuck");

		// KeyMapping.click(InputConstants.getKey(keyCode, scanCode));
		for (KeyMapping keymap : ModKeyBindings.INSTANCE.keys.values()){
			InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
			if (keymap.getCategory().equals(ModKeyBindings.CATEGORY_INVENTORY) &&
					keymap.getKey().equals(key)){
				KeyMapping.click(key);
			}
		}
		return true;  // Consume the event
    }
}
