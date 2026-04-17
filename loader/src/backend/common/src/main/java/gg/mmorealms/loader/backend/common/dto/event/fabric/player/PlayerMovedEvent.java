package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

@Getter
public class PlayerMovedEvent extends LocalEvent {

	private final ServerPlayer player;
	private final Vec3 position;
	private final double distance;

	public PlayerMovedEvent(ServerPlayer player, Vec3 position, double distance) {
		this.player = player;
		this.position = position;
		this.distance = distance;
	}

}
