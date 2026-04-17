package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

@Getter
public class PlayerEntityInteractEvent extends LocalRequest<EventResult> {

	private final ServerPlayer player;
	private final InteractionHand hand;
	private final Entity entity;

	public PlayerEntityInteractEvent(ServerPlayer player, InteractionHand hand, Entity entity) {
		super(EventResult.pass());
		this.player = player;
		this.hand = hand;
		this.entity = entity;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public Location getLocation() {
		return Location.of(entity.getX(), entity.getY(), entity.getZ());
	}

}
