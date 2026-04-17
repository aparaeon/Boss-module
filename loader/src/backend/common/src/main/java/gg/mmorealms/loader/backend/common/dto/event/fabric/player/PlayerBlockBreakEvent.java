package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class PlayerBlockBreakEvent extends LocalRequest<EventResult> {

	private Level world;
	private ServerPlayer player;
	private BlockPos pos;
	private BlockState state;

	public PlayerBlockBreakEvent(Level world, ServerPlayer player, BlockPos pos, BlockState state) {
		super(EventResult.pass());
		this.world = world;
		this.player = player;
		this.pos = pos;
		this.state = state;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public Location getLocation() {
		return Location.of(pos.getX(), pos.getY(), pos.getZ());
	}


}
