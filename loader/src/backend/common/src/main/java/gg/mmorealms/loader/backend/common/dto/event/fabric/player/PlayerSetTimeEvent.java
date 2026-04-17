package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerPlayer;

@Getter
@Setter
public class PlayerSetTimeEvent extends LocalRequest<ClientboundSetTimePacket> {
	ServerPlayer player;
	ClientboundSetTimePacket packet;

	public PlayerSetTimeEvent(ServerPlayer player, ClientboundSetTimePacket packet) {
		super(null);
		this.player = player;
		this.packet = packet;
	}
}