package net.lzdq.winterbridge.client.blockin;

import net.lzdq.winterbridge.ModConfig;
import net.lzdq.winterbridge.client.action.ActionHandler;
import net.lzdq.winterbridge.client.action.RotateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.*;

import java.util.ArrayList;
import java.util.List;

public class BlockInHandler {
	Minecraft mc;
	List<BlockPos> to_be_place;
	boolean rotating, finished;
	public BlockInHandler() {
		mc = Minecraft.getInstance();
		BlockPos pos = mc.player.blockPosition();
		to_be_place = new ArrayList<>();
		// Direction.from2DDataValue()
		for (int i = 0; i < 4; i++) {
			Direction d = Direction.from2DDataValue(i);
			to_be_place.add(pos.relative(d));
			to_be_place.add(pos.relative(d).above());
		}
		to_be_place.add(pos.above(2).east());
		to_be_place.add(pos.above(2).west());
		to_be_place.add(pos.above(2));
		rotating = false;
		finished = false;
		//Vec2 t = mc.player.getRotationVector();
		//mc.player.displayClientMessage(Component.literal("Fuck" + PlaceBlockHandler.getHitResult(t).getBlockPos()), false);
		//mc.player.displayClientMessage(Component.literal("True" + mc.hitResult.getLocation()), false);
		//mc.player.displayClientMessage(Component.literal("Look Vec3: " + mc.player.getLookAngle()), false);
	}
	public void tick(){
		if (rotating){
			if (RotateHandler.finished()){
				rotating = false;
				ActionHandler.placeBlock();
			} else return;
		}
		if (to_be_place.isEmpty()){
			mc.player.displayClientMessage(
					Component.literal("Block-in successful!")
							.withStyle(Style.EMPTY.withColor(ModConfig.getColorStartBridge())),
					true
			);
			finished = true;
			return ;
		}
		if (!(mc.player.getInventory().getSelected().getItem() instanceof BlockItem)){
			mc.player.displayClientMessage(
					Component.literal("Block-in failed: NOT HOLDING BLOCKS")
							.withStyle(Style.EMPTY.withColor(ModConfig.getColorCancelBridge())),
					true
			);
			finished = true;
			return ;
		}
		BlockPos pos = to_be_place.remove(0);
		if (!mc.level.getBlockState(pos).isAir())
			return ;
		Vec3 eye = mc.player.getEyePosition();
		boolean ok = false;
		Vec3 p = null;
		for (Direction d: Direction.values()){
			BlockPos base = pos.relative(d.getOpposite());
			if (mc.level.getBlockState(base).isAir())
				continue;
			if (eye.distanceToSqr(base.getCenter()) < eye.distanceToSqr(pos.getCenter()) + 1e-2)
				continue;
			for (int t = 0; t < 50; t++){
				p = base.getCenter().offsetRandom(mc.player.getRandom(),
						ModConfig.blockin_offset.get().floatValue());
				p = p.with(d.getAxis(), base.getCenter().add(pos.getCenter())
								.scale(0.5).get(d.getAxis()));
				/*
				mc.player.displayClientMessage(Component.literal("p: " +
						p.x + ", " +
						p.y + ", " +
						p.z), false);
				 */
				//HitResult hit = ProjectileUtil.getHitResult(mc.player, Entity::acceptsFailure);
				BlockHitResult hit = ActionHandler.getHitResult(p.subtract(eye));
				//mc.player.displayClientMessage(Component.literal("hit type: " + hit.getType().name()), false);
				if (hit.getType() == HitResult.Type.BLOCK){
					//mc.player.displayClientMessage(Component.literal("turn: " + t),  false);
					//mc.player.displayClientMessage(Component.literal("hit blockpos: " + hit.getBlockPos().toShortString()), false);
					//mc.player.displayClientMessage(Component.literal("base blockpos: " + base.toShortString()), false);
					//mc.player.displayClientMessage(Component.literal("hit direction: " + hit.getDirection().name()), false);
					//mc.player.displayClientMessage(Component.literal("base direction: " + d.name()), false);
					//mc.player.displayClientMessage(Component.literal("look vec3: " + p.subtract(eye).normalize()), false);
					//mc.player.displayClientMessage(Component.literal("hit location: " + hit.getLocation()), false);
					if (hit.getBlockPos().equals(base) && hit.getDirection() == d){
						ok = true;
						break;
					}
				}
			}
			if (ok) break;
		}
		if (ok) {
			//mc.player.displayClientMessage(Component.literal("ok"), false);
			Vec3 dir_vec = p.subtract(eye).normalize();
			double pitch = Math.toDegrees(Math.asin(-dir_vec.y));
			double yaw = Math.toDegrees(-Math.atan2(dir_vec.x, dir_vec.z));
			/*
			mc.player.displayClientMessage(Component.literal(
					"Rot: " +
					pitch + ", " +
					yaw), false);
			 */
			RotateHandler.init(new Vec2((float) pitch, (float) yaw),
					ModConfig.blockin_rotate_tick.get());
			rotating = true;
		}
	}
	public boolean isFinished(){
		return finished;
	}
	public void setCancelled(String cause){
		finished = true;
	}
}
