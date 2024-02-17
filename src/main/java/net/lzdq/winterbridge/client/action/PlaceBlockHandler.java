package net.lzdq.winterbridge.client.action;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceBlockHandler {
    public static boolean placeBlock(BlockHitResult hit){
        // Given hitResult, perform using item and return whether succeeded
        Minecraft mc = Minecraft.getInstance();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        InteractionResult result = mc.gameMode.useItemOn(mc.player, hand, hit);
        if (result.consumesAction()){
            if (result.shouldSwing()){
                mc.player.swing(hand);
                //mc.gameRenderer.itemInHandRenderer.itemUsed(hand);
            }
            return true;
        }
        return false;
    }
}
