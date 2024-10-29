package net.lzdq.winterbridge;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class Utils {
	public static boolean isBlock(Item item) {
		// Check whether the item is block EXCLUDE TNT
		return (item instanceof BlockItem && !item.equals(Items.TNT)) &&
			!item.equals(Items.CHEST) && !item.equals(Items.LADDER);
	}
	public static boolean isBlock(ItemStack item) {
		// Check whether the item is block EXCLUDE TNT
		return isBlock(item.getItem());
	}
}
