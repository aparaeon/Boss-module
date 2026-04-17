package gg.mmorealms.module.pokemon.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "user_starter")
@NoArgsConstructor
public class StarterData implements IDatabaseEntry<UUID> {

	@Id
	@NotNull
	private UUID uuid;

	public StarterData(@NotNull UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return null;
	}
}
