package gg.mmorealms.loader.backend.common.mixin;

import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerSetTimeEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerPacketListenerMixin {
	@Final
	@Shadow
	protected Connection connection;
	@Final
	@Shadow
	protected MinecraftServer server;

	@Shadow
	public abstract void send(Packet<?> packet);

	@Shadow
	public abstract void send(Packet<?> packet, @Nullable PacketSendListener listener);

	@Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
	private void onHandleSetTime(Packet<?> packet, CallbackInfo ci) {
		PacketListener listener = connection.getPacketListener();

		if (!(listener instanceof ServerGamePacketListenerImpl gameListener)) {
			return;
		}

		if (!(packet instanceof ClientboundSetTimePacket timePacket)) {
			return;
		}

		ServerPlayer serverPlayer = gameListener.player;

		ClientboundSetTimePacket result = new PlayerSetTimeEvent(serverPlayer, timePacket).fireSync(true);
		if (result != null) {
			send(result, null);
			ci.cancel();
		}
	}
}