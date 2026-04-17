package gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;


@Getter
public class PlayerAttackBlockEvent extends LocalRequest<EventResult> {
	private final ServerPlayer player;
	private final InteractionHand hand;
	private final BlockPos position;
	private final Direction face;

	public PlayerAttackBlockEvent(ServerPlayer player, InteractionHand hand, BlockPos blockPos, Direction face) {
		super(EventResult.pass());

		this.player = player;
		this.hand = hand;
		this.position = blockPos;
		this.face = face;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public Location getLocation() {
		return Location.of(position.getX(), position.getY(), position.getZ());
	}
}