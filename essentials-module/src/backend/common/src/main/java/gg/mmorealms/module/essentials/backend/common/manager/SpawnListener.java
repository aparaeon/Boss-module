package gg.mmorealms.module.essentials.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerJoinEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.manager.listener.Listener;
import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import gg.mmorealms.module.essentials.backend.common.dto.event.UserJoinMessageEvent;
import gg.mmorealms.module.essentials.common.dto.event.UserFirstJoinEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

@OnlyOn(servers = ServerType.SPAWN)
public class SpawnListener {
	@EventHandler(order = -400_000)
	private void onPlayerJoin(PlayerJoinEvent event) {
		ServerPlayer player = event.getPlayer();
		IUser user = IUser.getByUUID(player.getUUID());

		if (!user.getUsername().isEmpty()) {
			return;
		}

		user.setUsername(player.getName().getString());
		Listener.executeOnJoin.merge(player.getUUID(),
				new ArrayList<>(List.of(new UserFirstJoinEvent(player.getName().getString()))),
				(existing, incoming) -> {
					List<RedisRequest<?>> merged = new ArrayList<>(existing);
					merged.addAll(incoming);
					return merged;
				}
		);
	}

	@EventHandler
	private void onUserFirstJoinEvent(UserFirstJoinEvent event) {
		CommandSourceStack commandSourceStack = EssentialsBackendModule.instance().getServer()
				.createCommandSourceStack();

		EssentialsBackendModule.instance().getServer().getCommands().performPrefixedCommand(commandSourceStack,
				new MessageBuilder("/kits admin force_claim {user} Starter")
						.parse("user", event.getUsername())
						.parse()
		);

		EssentialsBackendModule.instance().getServer().getCommands().performPrefixedCommand(commandSourceStack,
				new MessageBuilder("/openstarterscreen {user}")
						.parse("user", event.getUsername())
						.parse()
		);


		UserJoinMessageEvent sendEvent = new UserJoinMessageEvent(event.getUsername());
		sendEvent.send();
		sendEvent.fireAsync();
	}

	@EventHandler(order = 200_000)
	private void onPlayerJoinForSpawnLocation(PlayerJoinEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());
		user.teleport(
				Location.builder(0.5, 60, 0.5) // TODO Config
						.pitch(0) // TODO Config
						.yaw(-90) // TODO Config
						.build()
		);
	}

}