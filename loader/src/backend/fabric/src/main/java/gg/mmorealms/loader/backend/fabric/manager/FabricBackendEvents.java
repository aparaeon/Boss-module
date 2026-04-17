package gg.mmorealms.loader.backend.fabric.manager;

import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerInteractArmorStandEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.GameMessageEvent;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;

public class FabricBackendEvents {

	public void registerEvents() {
		ServerMessageEvents.ALLOW_GAME_MESSAGE.register(
				(MinecraftServer server, Component message, boolean overlay) -> {
					GameMessageEvent event = new GameMessageEvent(server, message, overlay);
					return Boolean.TRUE.equals(event.fireSync());
				}
		);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (entity instanceof ArmorStand) {
				PlayerInteractArmorStandEvent event = new PlayerInteractArmorStandEvent((ServerPlayer) player, world, hand, entity, hitResult);
				return event.fireSync().asMinecraft();
			}

			return InteractionResult.PASS;
		});
	}
}