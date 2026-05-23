package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import com.mojang.brigadier.ParseResults;
import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

@Getter
public class PlayerCommandEvent extends LocalRequest<EventResult> {

	private final ServerPlayer player;
	private final ParseResults<CommandSourceStack> results;

	public PlayerCommandEvent(ServerPlayer player, ParseResults<CommandSourceStack> results) {
		super(EventResult.pass());
		this.player = player;
		this.results = results;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}
}
