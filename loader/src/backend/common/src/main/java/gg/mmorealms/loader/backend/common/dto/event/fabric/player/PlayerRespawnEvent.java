package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@AllArgsConstructor
@Getter
public class PlayerRespawnEvent extends LocalEvent {

	private ServerPlayer newPlayer;
	private boolean conqueredEnd;
	private Entity.RemovalReason removalReason;

}
