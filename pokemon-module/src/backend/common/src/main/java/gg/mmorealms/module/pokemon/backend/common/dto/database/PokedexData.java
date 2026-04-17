package gg.mmorealms.module.pokemon.backend.common.dto.database;

import com.google.gson.JsonObject;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokedex;
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

import java.util.UUID;

/**
 * This database table does not need to get cached as it should only be read and written on player join / leave and it
 * needs to have access to the most access data.
 */
@Entity(name = "pokedex_data")
@NoArgsConstructor
@Getter
@Setter
public class PokedexData implements IDatabaseEntry<UUID> {

	@Id
	@NotNull
	private UUID uuid;
	@JdbcTypeCode(SqlTypes.JSON)
	private JsonObject data = new JsonObject();

	public PokedexData(UUID uuid, IPokedex pokedex) {
		this.uuid = uuid;

		this.data = pokedex.serialize();
	}

	public PokedexData(UUID uuid) {
		this(uuid, PokemonBackendModule.instance().getPlatformImplementation().createEmptyPokedex(uuid));
	}

	public PokedexData(ServerPlayer player) {
		this(
				player.getUUID(),
				PokemonBackendModule.instance().getPlatformImplementation().getPokedex(player)
		);
	}

	public static @NotNull PokedexData get(ServerPlayer player) {
		return get(player.getUUID());
	}

	public static @NotNull PokedexData get(UUID uuid) {
		if(CommonLoader.DUMMY_MODE){
			Logger.warn("Attempting to get PokedexData for uuid " + uuid + " while in dummy mode, returning empty PokedexData");
			return new PokedexData(uuid);
		}

		try (Session session = PokemonBackendModule.instance().getDatabaseManager().getSessionFactory().openSession()) {
			PokedexData cobblemonData = session.get(PokedexData.class, uuid);

			if (cobblemonData == null) {
				return new PokedexData(uuid);
			}

			return cobblemonData;
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

	public IPokedex deserialize() {
		return PokemonBackendModule.instance().getPlatformImplementation().deserializePokedex(this.uuid, this.data);
	}
}
