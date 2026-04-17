package gg.mmorealms.module.user_data.backend.common.database;

import com.google.gson.JsonElement;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.backend.common.utils.ItemUtils;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.user_data.backend.common.UserDataBackendModule;
import gg.mmorealms.module.user_data.backend.common.dto.GamemodeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.hibernate.Session;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Entity(name = "user_data")
@Getter
@Setter
@NoArgsConstructor
public class UserData implements IDatabaseEntry<UUID> {

	private static final ConcurrentHashMap<UUID, Long> evictionTimestamps = new ConcurrentHashMap<>();
	private static volatile CancelableTimeTask evictionTimestampsCleaner = null;
	@Id
	@jakarta.validation.constraints.NotNull
	private @Getter UUID uuid;
	@Range(min = 0, max = 20)
	private double health = 20;
	@Range(min = 0, max = 20)
	private float exhaustion = 0;
	@Range(min = 0, max = 20)
	private int foodLevel = 20;
	@Range(min = 0, max = 20)

	private float saturationLevel = 20;
	private int totalExperience = 0;
	private int experienceLevel = 0;
	@JdbcTypeCode(SqlTypes.JSON)
	private List<JsonElement> mainInventory = new ArrayList<>();
	@JdbcTypeCode(SqlTypes.JSON)
	private List<JsonElement> armourInventory = new ArrayList<>();
	@JdbcTypeCode(SqlTypes.JSON)
	private List<JsonElement> offHandInventory = new ArrayList<>();
	@JdbcTypeCode(SqlTypes.JSON)
	private List<JsonElement> enderChest = new ArrayList<>();
	private GamemodeType gamemode = GamemodeType.SURVIVAL;
	@JdbcTypeCode(SqlTypes.JSON)
	private JsonElement advancements;
	private transient boolean isEvictionSave = false;

	public UserData(ServerPlayer player, boolean evictFromCache) {
		this.isEvictionSave = evictFromCache;
		this.uuid = player.getUUID();

		this.health = player.getHealth();
		this.exhaustion = player.getFoodData().getExhaustionLevel();
		this.foodLevel = player.getFoodData().getFoodLevel();
		this.saturationLevel = player.getFoodData().getSaturationLevel();
		this.totalExperience = player.totalExperience;
		this.experienceLevel = player.experienceLevel;

		this.mainInventory = CodecUtils.serialize(ItemStack.CODEC, player.getInventory().items);
		this.armourInventory = CodecUtils.serialize(ItemStack.CODEC, player.getInventory().armor);
		this.offHandInventory = CodecUtils.serialize(ItemStack.CODEC, player.getInventory().offhand);
		this.enderChest = CodecUtils.serialize(ItemStack.CODEC, player.getEnderChestInventory().getItems());

		this.gamemode = GamemodeType.fromGameType(player.gameMode.getGameModeForPlayer());
		this.advancements = CodecUtils.serialize(player.getAdvancements().codec, player.getAdvancements().asData());

		if (evictFromCache) {
			evictionTimestamps.put(this.uuid, System.currentTimeMillis());
			player.getInventory().clearContent();
			player.getEnderChestInventory().clearContent();
		}
	}

	public static void init() {
		evictionTimestampsCleaner = ScheduleUtils.runTaskTimer(() -> {
			long now = System.currentTimeMillis();
			evictionTimestamps.entrySet().removeIf(entry -> now - entry.getValue() > 10_000L);
		}, Time.seconds(30));
	}

	public static UserData get(ServerPlayer player) {
		UserData userData = get(player.getUUID());

		if (userData == null) {
			return new UserData(player, false);
		}

		return userData;
	}

	public static @Nullable UserData get(UUID uuid) {
		if (CommonLoader.DUMMY_MODE) {
			Logger.warn("Trying to get UserData for uuid " + uuid + " while in dummy mode, returning null");
			return null;
		}

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			return session.get(UserData.class, uuid);
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

	public void apply(ServerPlayer player) {
		player.setHealth((float) this.health);

		player.getFoodData().setExhaustion(this.exhaustion);
		player.getFoodData().setFoodLevel(this.foodLevel);
		player.getFoodData().setSaturation(this.saturationLevel);

		player.setExperienceLevels(this.experienceLevel);
		player.setExperiencePoints(this.totalExperience);

		player.getInventory().items.clear();
		player.getInventory().armor.clear();
		player.getInventory().offhand.clear();

		ItemUtils.setItemStackList(player.getInventory().items, CodecUtils.deserialize(ItemStack.CODEC, this.mainInventory, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)));
		ItemUtils.setItemStackList(player.getInventory().armor, CodecUtils.deserialize(ItemStack.CODEC, this.armourInventory, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)));
		ItemUtils.setItemStackList(player.getInventory().offhand, CodecUtils.deserialize(ItemStack.CODEC, this.offHandInventory, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)));
		ItemUtils.setItemStackList(player.getEnderChestInventory().getItems(), CodecUtils.deserialize(ItemStack.CODEC, this.enderChest, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)));

		player.setGameMode(this.getGamemode().toGameType());

		ServerAdvancementManager advancementManager = UserDataBackendModule.instance().getServer().getAdvancements();
		PlayerAdvancements playerAdvancements = UserDataBackendModule.instance().getServer().getPlayerList().getPlayerAdvancements(player);
		PlayerAdvancements.Data playerAdvancementsData = CodecUtils.deserialize(playerAdvancements.codec, this.advancements, CodecUtils.CodecErrorProcessor.ofNull());

		if (playerAdvancementsData != null) {
			try {
				playerAdvancements.applyFrom(advancementManager, playerAdvancementsData);
			} catch (Throwable throwable) {
				Logger.warn(throwable);
			}
		}
	}

	@Override
	public synchronized void save() throws DatabaseSaveException {
		if (CommonLoader.DUMMY_MODE) {
			Logger.debug("Dummy mode enabled, skipping saving " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
			return;
		}

		if (!isEvictionSave) {
			Long evictedAt = evictionTimestamps.get(this.uuid);
			if (evictedAt != null && System.currentTimeMillis() - evictedAt < 5_000L) {
				Logger.debug("Skipping stale autosave for " + uuid + " (evictedAt=" + evictedAt + ", delta=" + (System.currentTimeMillis() - evictedAt) + "ms)");
				return;
			}
			Logger.debug("Autosave proceeding for " + uuid + " | evictedAt=" + evictedAt + " | inventorySize=" + mainInventory.size());
		} else {
			Logger.debug("Eviction save proceeding for " + uuid + " | inventorySize=" + mainInventory.size());
		}

		Logger.debug("Saving " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());

		DatabaseManager.instance().getSessionFactory().inTransaction(transaction -> {
			Object existingEntity = transaction.get(this.getClass(), getIdentifier());
			if (existingEntity == null) {
				Logger.debug("Persisting " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
				transaction.persist(this);
			} else {
				Logger.debug("Merging " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
				transaction.merge(this);
			}
		});
	}
}
