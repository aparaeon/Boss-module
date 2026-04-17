package gg.mmorealms.module.pokedex_rewards.backend.common.dto;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokedex_rewards.backend.common.PokedexRewardsBackendModule;
import gg.mmorealms.module.pokedex_rewards.backend.common.dto.enums.RewardStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "pokedex_rewards_data")
@NoArgsConstructor
@Getter
@Setter
public class PokedexRewardsData implements IDatabaseEntry<UUID> {

	@Id
	@NotNull
	private UUID uuid;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<Double> progress = new ArrayList<>();

	public PokedexRewardsData(UUID uuid) {
		this.uuid = uuid;
	}

	public PokedexRewardsData(ServerPlayer player) {
		this(player.getUUID());
	}

	public static @NotNull PokedexRewardsData get(IUser user) {
		return get(user.getUUID());
	}

	public static @NotNull PokedexRewardsData get(ServerPlayer player) {
		return get(player.getUUID());
	}

	public static @NotNull PokedexRewardsData get(UUID uuid) {
		try (Session session = PokedexRewardsBackendModule
				.instance()
				.getDatabaseManager()
				.getSessionFactory()
				.openSession()) {

			PokedexRewardsData data = session.get(PokedexRewardsData.class, uuid);
			return (data != null) ? data : new PokedexRewardsData(uuid);
		}
	}


	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return null;
	}

	public @NotNull boolean isClaimed(@NotNull double milestone) {
		return this.progress.contains(milestone);
	}

	public void markClaimed(double milestone) {
		this.progress = new ArrayList<>(this.progress);
		this.progress.add(milestone);
		try {
			save();
		} catch (DatabaseSaveException e) {
			Logger.error(new MessageBuilder("{uuid} tried to claim pokedex rewards for {milestone} milestone but there was a save exception")
					.parse("uuid", uuid)
					.parse("milestone", milestone)
			);

			Logger.error(e);
			return;
		}

		Logger.info(new MessageBuilder("{uuid} claimed pokedex rewards for {milestone} milestone")
				.parse("uuid", uuid)
				.parse("milestone", milestone)
		);
	}

	public RewardStatus getStatus(double milestone) {
		if (isClaimed(milestone)) {
			return RewardStatus.CLAIMED;
		}

		User user = User.unsafeGetByUUID(this.uuid);

		if (user == null) {
			return RewardStatus.LOCKED;
		}

		IPokedex pokedex = IPokedex.get(user.getPlayer());

		if ((int) (pokedex.getMaxSize() * milestone) <= pokedex.getSize()) {
			return RewardStatus.AVAILABLE;
		}
		return RewardStatus.LOCKED;
	}

}