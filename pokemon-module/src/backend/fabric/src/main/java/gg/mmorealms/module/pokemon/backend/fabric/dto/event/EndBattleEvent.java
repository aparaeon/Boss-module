package gg.mmorealms.module.pokemon.backend.fabric.dto.event;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;

@Getter
@AllArgsConstructor
public class EndBattleEvent extends LocalEvent {
	private ServerPlayer player;
}
