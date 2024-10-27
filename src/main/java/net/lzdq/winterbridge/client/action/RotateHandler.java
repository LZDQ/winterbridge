package net.lzdq.winterbridge.client.action;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec2;

public class RotateHandler {
	static Minecraft mc;
	static Vec2 from, dest, points[];
	static int n=0, t=1;  // Total and current tick
	public static void init(Vec2 destination, int maxtick){
		mc = Minecraft.getInstance();
		from = mc.player.getRotationVector();
		dest = destination;
		Vec2 vec_offset = dest.add(from.negated());
		float yrot = vec_offset.y;
		yrot %= 360;
		if (yrot > 180) yrot -= 360;
		else if (yrot < -180) yrot += 360;
		//mc.player.connection.sendChat("YRot: " + yrot);
		vec_offset = new Vec2(vec_offset.x, yrot);
		Vec2 vec_orth = new Vec2(-vec_offset.y, vec_offset.x);
		if (Math.random() < 0.5)
			vec_orth = vec_orth.negated();
		// Generate points
		// Lane follows f(x)=sin(x^2)/ [20, 30]
		n = Math.min(maxtick, (int) (vec_offset.length() / 2));
		double end = Math.sqrt(Math.PI), scale = Math.random() * 10 + 20;
		points = new Vec2[n+1];
		points[0] = from;
		for(int i=1; i<=n; i++){
			double x = end * i / n;
			double y = Math.sin(x * x) / scale;
			points[i] = from.add(vec_offset.scale((float) i / n).add(vec_orth.scale((float) y)));
		}
		t = 1;
		//gap = 20;
		//left_gap = gap;
	}
	public static void tick(){
		// Rotate and return whether continue
		if (t > n) return ;
		mc.player.setXRot(points[t].x);
		mc.player.setYRot(points[t].y);
		t++;
	}
	public static boolean finished(){
		return t > n;
	}
	public static void setCancelled(){
		t = n + 1;
	}
}
