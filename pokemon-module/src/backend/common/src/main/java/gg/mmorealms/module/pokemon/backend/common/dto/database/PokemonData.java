package gg.mmorealms.module.pokemon.backend.common.dto.database;

import com.google.gson.JsonObject;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import org.hibernate.Session;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This database table does not need to get cached as it should only be read and written on player join / leave and it
 * needs to have access to the most access data.
 */
@Entity(name = "pokemon_data")
@NoArgsConstructor
@Getter
@Setter
public class PokemonData implements IDatabaseEntry<UUID> {

	@Id
	@NotNull
	private UUID uuid;
	@JdbcTypeCode(SqlTypes.JSON)
	private JsonObject pcData = new JsonObject();
	@JdbcTypeCode(SqlTypes.JSON)
	private JsonObject partyData = new JsonObject();

	public PokemonData(ServerPlayer player) {
		this.uuid = player.getUUID();

		this.pcData = PokemonBackendModule.instance().getPlatformImplementation().getPC(player).serialize();
		this.partyData = PokemonBackendModule.instance().getPlatformImplementation().getParty(player).serialize();
	}

	public static @NotNull PokemonData get(ServerPlayer player) {
		PokemonData pokemonData = get(player.getUUID());

		if (pokemonData == null) {
			return new PokemonData(player);
		}

		return pokemonData;
	}

	public static @Nullable PokemonData get(UUID uuid) {
		if(CommonLoader.DUMMY_MODE){
			Logger.warn("Trying to get PokemonData for uuid " + uuid + " while in dummy mode, returning null");
			return null;
		}

		try (Session session = PokemonBackendModule.instance().getDatabaseManager().getSessionFactory().openSession()) {
			return session.get(PokemonData.class, uuid);
		}
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public SyncedDatabaseLoader<UUID, ?, ?, ?> getLoader() {
		return null;
	}


}
