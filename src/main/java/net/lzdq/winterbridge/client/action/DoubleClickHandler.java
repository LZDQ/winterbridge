package net.lzdq.winterbridge.client.action;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.WinterBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DoubleClickHandler {
	Minecraft mc;
	BlockPos baseBlockPos, hitBlockPos, tgtBlockPos;
	Direction direction;
	boolean finished;
	long startTime;
	public DoubleClickHandler() {
		mc = Minecraft.getInstance();
		if (mc.hitResult.getType() == HitResult.Type.BLOCK) {
			startTime = System.currentTimeMillis();
			BlockHitResult hit = (BlockHitResult) mc.hitResult;
			baseBlockPos = hit.getBlockPos();
			direction = hit.getDirection();
			WinterBridge.LOGGER.info("Double click direction: {}", direction.name());
			hitBlockPos = baseBlockPos.relative(direction);
			tgtBlockPos = hitBlockPos.relative(direction);
			finished = false;
			ActionHandler.placeBlock();
		} else {
			finished = true;
		}
	}

	public boolean isFinished(){
		return finished || System.currentTimeMillis() > startTime + ModConfig.timeout_doubleclick.get();
	}
	
	public void tick(){
		if (mc.hitResult.getType() == HitResult.Type.BLOCK){
			BlockHitResult blockHitResult = (BlockHitResult) mc.hitResult;
			if (blockHitResult.getBlockPos().relative(blockHitResult.getDirection()).equals(tgtBlockPos)){
				ActionHandler.placeBlock();
				finished = true;
			}
		}
	}
}
