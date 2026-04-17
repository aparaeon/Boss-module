package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

@Getter
public class PlayerPreDeathEvent extends LocalRequest<EventResult> {

	private final ServerPlayer player;
	private final DamageSource source;

	public PlayerPreDeathEvent(ServerPlayer player, DamageSource source) {
		super(EventResult.pass());
		this.player = player;
		this.source = source;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}
}
