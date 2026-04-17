package gg.mmorealms.module.core.common.dto;

import gg.mmorealms.loader.common.dto.event.impl.UserServerRequest;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PlayerList {
	private final List<PlayerEntry> playersList;

	public List<PlayerEntry> getList() {
		return playersList;
	}

	public PlayerList() {
		this(new ArrayList<>());
	}

	public @Nullable String getServer(UUID uuid) {
		return new UserServerRequest(uuid).sendAndGet();
	}

	public @Nullable String getServer(String username) {
		return new UserServerRequest(username).sendAndGet();
	}

	@SuppressWarnings("unused")
	public @Nullable String getUsername(UUID uuid) {
		for (PlayerEntry player : playersList) {
			if (player.uuid().equals(uuid)) {
				return player.username();
			}
		}
		return null;
	}

	public @Nullable UUID getUUID(String username) {
		for (PlayerEntry player : playersList) {
			if (player.username().equals(username)) {
				return player.uuid();
			}
		}

		return null;
	}

	public boolean isOnline(@NotNull UUID uuid) {
		for (PlayerEntry player : playersList) {
			if (player.uuid.equals(uuid)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unused")
	public boolean isOnline(@NotNull String username) {
		for (PlayerEntry player : playersList) {
			if (player.username.equals(username)) {
				return true;
			}
		}

		return false;
	}

	public List<String> getUsernames() {
		return playersList.stream().map(PlayerEntry::username).toList();
	}

	public record PlayerEntry(UUID uuid, String username) {
	}
}