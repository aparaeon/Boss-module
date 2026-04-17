package gg.mmorealms.loader.backend.common.dto.event.fabric.server;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

@Getter
public class GameMessageEvent extends LocalRequest<Boolean> {

	private final MinecraftServer server;
	private final Component message;
	private final boolean overlay;

	public GameMessageEvent(MinecraftServer server, Component message, boolean overlay) {
		super(true);

		this.server = server;
		this.message = message;
		this.overlay = overlay;
	}
}
