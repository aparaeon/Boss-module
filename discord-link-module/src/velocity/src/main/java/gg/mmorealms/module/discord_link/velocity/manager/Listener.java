package gg.mmorealms.module.discord_link.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import gg.mmorealms.module.discord_link.velocity.database.DiscordLinkedUser;

public class Listener {

	private @Inject DiscordLinkerManager discordLinkerManager;

	// TODO add a way to update the discord ranks as soon as the player received the rank from the store
	@EventHandler(order = 100_000)
	public void onPlayerJoin(PostLoginEvent event) {
		DiscordLinkedUser user = DiscordLinkedUser.getByPlayer(event.getPlayer());
		ScheduleUtils.runTaskAsync(() -> this.discordLinkerManager.syncRanks(user));
	}

}
