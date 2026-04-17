package gg.mmorealms.module.warps.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.warps.backend.common.dto.Warp;

import java.util.ArrayList;
import java.util.List;

public class WarpsConfig {

	public List<Warp> warps = new ArrayList<>(List.of(
			new Warp(
					"Crates", ServerType.SPAWN,
					new Location("minecraft:overworld", 24.5, 60, -22.5, -1.5f, 135.5f)
			),
			new Warp(
					"Heal", ServerType.SPAWN,
					new Location("minecraft:overworld", 55.5, 59, 34.5, -1f, 0.5f)
			),
			new Warp(
					"PokeCenter", ServerType.SPAWN,
					new Location("minecraft:overworld", 55.5, 59, 34.5, -1f, 0.5f)
			)
	));

	public Lang lang = new Lang();

	public static class Lang {
		public String headerWarps = "=====   Warps   =====<newline>";
		public MessageBuilder entryWarps = new MessageBuilder("{name}<newline>");

		public String invalidWarp = "<red>There is no warp with this name!";
		public MessageBuilder successWarp = new MessageBuilder("<green>Teleported to <grey>{name}<green> warp!");
		public String successDelete = "<green>Successfully deleted warp!";
		public String successAdd = "<green>Successfully added new warp!";
		public String successOverrideAdd = "<green>There already was a warp with this name! Updated location";
	}

	public Warp getWarp(String name) {
		for (Warp warp : this.warps) {
			if (warp.getName().equalsIgnoreCase(name)) {
				return warp;
			}
		}

		return null;
	}

	public List<String> getWarpNames() {
		return this.warps.stream().map(Warp::getName).toList();
	}
}
