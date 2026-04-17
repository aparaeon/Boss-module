package gg.mmorealms.module.discord_link.velocity.database;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.manager.DiscordLinkedUserDatabaseLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity(name = "discord_linked_users")
@Table(indexes = {
		@Index(columnList = "discordID")
})
public class DiscordLinkedUser implements IDatabaseEntry<UUID> {

	@Id
	private @Unique UUID uuid;
	private @Unique long discordID;

	private @Setter boolean claimedRewards = false;
	private @Setter boolean claimedNitroRewards = false;

	public DiscordLinkedUser(UUID uuid, long discordID) {
		this.uuid = uuid;
		this.discordID = discordID;

		this.getLoader().cache(uuid, this);
		DiscordLinkVelocityModule.instance().getDiscordLinkerManager().award(this);
	}

	@Override
	public UUID getIdentifier() {
		return uuid;
	}

	@Override
	public DiscordLinkedUserDatabaseLoader getLoader() {
		return DiscordLinkVelocityModule.instance().getDiscordLinkedUserDatabaseLoader();
	}

	public static DiscordLinkedUser getByPlayer(Player player) {
		return getByUUID(player.getUniqueId());
	}

	public static DiscordLinkedUser getByUUID(UUID uuid) {
		return DiscordLinkVelocityModule.instance().getDiscordLinkedUserDatabaseLoader().getByIdentifier(uuid);
	}

	public static DiscordLinkedUser getByDiscordID(long discordID) {
		return DiscordLinkVelocityModule.instance().getDiscordLinkedUserDatabaseLoader().getByIndexedFiled("discordID", discordID);
	}

	public @Nullable Player getPlayer() {
		return DiscordLinkVelocityModule.instance().getProxy().getPlayer(uuid).orElse(null);
	}

	public @Nullable Member getDiscordMember() {
		return DiscordLinkVelocityModule.instance().getDiscordLinkingBot().getGuild().retrieveMemberById(this.discordID).complete();
	}
}
