package gg.mmorealms.loader.common.dto.time;

import com.raduvoinea.utils.generic.Time;

public class MinecraftTickUnit implements Time.UnitProvider {

	public static MinecraftTickUnit INSTANCE;

	static {
		MinecraftTickUnit.INSTANCE = new MinecraftTickUnit();
	}

	@Override
	public String getSerializedName() {
		return "MINECRAFT_TICKS";
	}

	@Override
	public long toMilliseconds() {
		return 50;
	}

}
