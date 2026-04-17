package gg.mmorealms.module.discord_link.velocity.manager;

import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.database.DiscordLinkedUser;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordLinkedUserDatabaseLoader extends DatabaseLoader<UUID, DiscordLinkedUser, DiscordLinkedUser> {

	public DiscordLinkedUserDatabaseLoader() {
		super(DiscordLinkedUser.class);
	}

	@Override
	protected boolean shouldClearCache(@NotNull UUID uuid, @NotNull DiscordLinkedUser discordLinkedUser) {
		return DiscordLinkVelocityModule.instance().getProxy().getPlayer(uuid).isEmpty();
	}

}
