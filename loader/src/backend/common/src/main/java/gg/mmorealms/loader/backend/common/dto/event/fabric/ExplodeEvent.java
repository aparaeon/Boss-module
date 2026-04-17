package gg.mmorealms.loader.backend.common.dto.event.fabric;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;

@Getter
public class ExplodeEvent extends LocalRequest<EventResult> {

	private final ServerLevel world;
	private final Explosion explosion;

	public ExplodeEvent(ServerLevel world, Explosion explosion) {
		super(EventResult.pass());
		this.world = world;
		this.explosion = explosion;
	}


	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}


}
