package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;

@Getter
@AllArgsConstructor
public class PlayerEditBookEvent extends LocalEvent {
	private ServerPlayer player;
	private int itemIndex;
}
