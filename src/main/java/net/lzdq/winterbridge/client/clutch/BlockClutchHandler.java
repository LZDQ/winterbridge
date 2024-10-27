package net.lzdq.winterbridge.client.clutch;

import net.lzdq.winterbridge.client.action.ActionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockClutchHandler extends AbstractClutchHandler{
	Direction dir_go;
	BlockHitResult hit_forward;
	boolean finished, fail;
	public BlockClutchHandler(){
		super();
		dir_go = mc.player.getDirection();
		//mc.player.connection.sendChat(dir_go.getName());	There are only 4 directions, not up and down
		fail = true;
		finished = false;
		BlockPos pos = mc.player.blockPosition();  // BlockPos of feet
		for (int i = 1; i < 5; i++){
			BlockHitResult testHitResult = calcHitResult(pos.below(i));
			if (testHitResult != null){
				base_pos = pos.below(i);
				hit = testHitResult;
				fail = false;
				break;
			}
		}
		if (fail) return ;
		BlockHitResult testHitResult = calcHitResult(base_pos.relative(dir_go));
		if (testHitResult != null)
			hit_forward = testHitResult;
	}
	BlockHitResult calcHitResult(BlockPos pos){
		for (Direction d : Direction.values()){
			if (d == dir_go.getOpposite()) continue;
			BlockPos base = pos.relative(d);
			if (!mc.level.getBlockState(base).isAir()){
				Vec3 loc = Vec3.atCenterOf(base);
				loc = loc.add(Vec3.atLowerCornerOf(d.getOpposite().getNormal()).scale(0.5));
				if (d.getStepY() == 0){
					loc = loc.add(Vec3.atLowerCornerOf(d.getClockWise().getNormal())
							.scale(Math.random() * 0.5 - 0.25));
					loc = loc.add(0, Math.random() * 0.5 - 0.25, 0);
				}
				return new BlockHitResult(loc, d.getOpposite(), base, false);
			}
		}
		return null;
	}
	@Override
	public void tick(){
		if (isFinished()) return ;
		if (ActionHandler.placeBlock(hit)){
			if (hit_forward != null){
				hit = hit_forward;
				hit_forward = null;
			} else finished = true;
		} else fail = true;
	}
	@Override
	public boolean isFinished(){
		return fail || finished;
	}
}
