package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Getter
@AllArgsConstructor
public class PlayerPreJoinEvent extends LocalEvent {

	private final ServerGamePacketListenerImpl handler;
	private final MinecraftServer server;


	public ServerPlayer getPlayer() {
		return handler.getPlayer();
	}
}
