package net.lzdq.winterbridge.client.action;

import static net.lzdq.winterbridge.Utils.*;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ActionHandler {
    public static boolean placeBlock(BlockHitResult hit){
        // Given hitResult, perform using item and return whether succeeded
        Minecraft mc = Minecraft.getInstance();
        // if (!isBlock(mc.player.getInventory().getSelected())) return false;
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
    public static boolean placeBlock(){
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        //mc.player.displayClientMessage(Component.literal("Hi"), false);
        if (hit.getType() == HitResult.Type.BLOCK)
            return placeBlock((BlockHitResult) hit);
        //mc.player.displayClientMessage(Component.literal("Fuck"), false);
        return false;
    }
    public static BlockHitResult getHitResult(Vec3 dir_vec){
        Minecraft mc = Minecraft.getInstance();
        dir_vec = dir_vec.normalize();
        double dist = mc.player.getBlockReach();
        Vec3 start_vec = mc.player.getEyePosition();
        Vec3 end_vec = start_vec.add(dir_vec.scale(dist));
        return mc.level.clip(new ClipContext(start_vec, end_vec, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, mc.player));
    }
    public static void useItem(){
        KeyMapping.click(Minecraft.getInstance().options.keyUse.getKey());
    }
}
