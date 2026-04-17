package gg.mmorealms.module.gyms.backend.common.dto.gym;

import com.google.gson.JsonElement;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.dto.CooldownInfo;
import gg.mmorealms.module.gyms.backend.common.dto.database.GymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.enums.GymCooldown;
import gg.mmorealms.module.gyms.backend.common.dto.gym.clauses.GymInBattleClauses;
import gg.mmorealms.module.gyms.backend.common.dto.gym.clauses.GymPreBattleClause;
import gg.mmorealms.module.gyms.backend.common.dto.gym.clauses.GymPreBattleClauses;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.ITrainer;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.TrainerInfo;
import gg.mmorealms.module.pokemon.common.dto.Response;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Gym {
	private final String name;
	private final String id;
	private final MessageBuilderList description;
	private final List<String> requiredGyms;
	private final Location spawnLocation;

	private final TrainerInfo trainerInfo;

	private final JsonElement displayItem;
	private final JsonElement activeBadgeItem;
	private final JsonElement inactiveBadgeItem;

	private final List<String> firstTimeBonusRewards;
	private final List<String> regularRewardCommands;

	private final List<String> gymClausesAsString;

	private final List<String> firstWinPlayerMessage;
	private final List<String> regularWinPlayerMessage;

	@Setter
	private transient ITrainer trainer;
	private transient GymInBattleClauses gymInBattleClauses;
	private transient List<GymPreBattleClause> gymPreBattleClauses;

	public final MessageBuilder playerWonBattleMessage;
	public final MessageBuilder playerLostBattleMessage;

	// TODO: TO BE REMOVED, after implementing the mailbox system
	private final int freeSpaceRequiredFirstWin;
	private final int freeSpaceRequiredNormalWin;

	private final Time lossCooldown;
	private final Time winCooldown;

	public Gym(String name, String id, MessageBuilderList description, List<String> requiredGyms,
	           List<String> gymClausesAsString, Location spawnLocation,
	           List<String> firstTimeBonusRewards, List<String> regularRewardCommands,
	           TrainerInfo trainerInfo,
	           ItemStack displayItem, CustomModelData customModelData,
	           ItemStack badgeItem, CustomModelData activeBadgeData, CustomModelData inactiveBadgeData, List<String> firstWinPlayerMessage, List<String> regularWinPlayerMessage,
	           MessageBuilder playerWonBattleMessage, MessageBuilder playerLostBattleMessage,
	           Time lossCooldown, Time winCooldown,
	           int freeSpaceRequiredFirstWin, int freeSpaceRequiredNormalWin) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.requiredGyms = requiredGyms;
		this.spawnLocation = spawnLocation;
		this.trainerInfo = trainerInfo;
		this.gymClausesAsString = gymClausesAsString;
		this.firstTimeBonusRewards = firstTimeBonusRewards;
		this.regularRewardCommands = regularRewardCommands;
		this.firstWinPlayerMessage = firstWinPlayerMessage;
		this.regularWinPlayerMessage = regularWinPlayerMessage;
		this.playerWonBattleMessage = playerWonBattleMessage;
		this.playerLostBattleMessage = playerLostBattleMessage;
		this.lossCooldown = lossCooldown;
		this.winCooldown = winCooldown;
		this.freeSpaceRequiredFirstWin = freeSpaceRequiredFirstWin;
		this.freeSpaceRequiredNormalWin = freeSpaceRequiredNormalWin;

		if (customModelData != null) {
			displayItem.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
		}
		this.displayItem = CodecUtils.serialize(ItemStack.CODEC, displayItem);

		if (activeBadgeData != null) {
			badgeItem.set(DataComponents.CUSTOM_MODEL_DATA, activeBadgeData);
		}
		this.activeBadgeItem = CodecUtils.serialize(ItemStack.CODEC, badgeItem);

		if (inactiveBadgeData != null) {
			badgeItem.set(DataComponents.CUSTOM_MODEL_DATA, inactiveBadgeData);
		}
		this.inactiveBadgeItem = CodecUtils.serialize(ItemStack.CODEC, badgeItem);
	}

	public void teleportUser(IUser user) {
		user.send(IServerLocation.of(ServerType.GYMS), spawnLocation);
	}

	public ItemStack getDisplayItem() {
		return CodecUtils.deserialize(ItemStack.CODEC, displayItem, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public ItemStack getActiveBadgeItem() {
		return CodecUtils.deserialize(ItemStack.CODEC, activeBadgeItem, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public ItemStack getInactiveBadgeItem() {
		return CodecUtils.deserialize(ItemStack.CODEC, inactiveBadgeItem, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));

	}

	public void reassignTrainer() {
		trainer = GymsBackendModule.instance().getTrainerPlatformImplementation().spawn(trainerInfo);
	}

	public void refreshClauses() {
		if (gymClausesAsString == null || gymClausesAsString.isEmpty()) {
			return;
		}

		gymPreBattleClauses = new ArrayList<>();
		gymInBattleClauses = new GymInBattleClauses();
		for (String clause : gymClausesAsString) {
			GymPreBattleClause gymPreBattleClause = GymPreBattleClauses.getByName(clause);
			if (gymPreBattleClause != null) {
				gymPreBattleClauses.add(gymPreBattleClause);
				continue;
			}

			gymInBattleClauses.apply(clause);
		}
	}

	public Response canBattle(UUID playerUuid) {
		boolean passedRequiredGymCheck = requiredGymsCheck(playerUuid);
		if (!passedRequiredGymCheck) {
			return new Response(false, GymsBackendModule.instance().getConfig().lang.playerTriedToBattleUnreachableGym);
		}

		boolean passedEnoughFreeSpaceCheck = enoughFreeSpaceCheck(playerUuid);
		if (!passedEnoughFreeSpaceCheck) {
			return new Response(false, GymsBackendModule.instance().getConfig().lang.playerNotEnoughInventorySpace);
		}

		return new Response(true, (String) null);
	}

	public CooldownInfo getActiveCooldownInfo(UUID playerUuid) {
		IBackendCooldowns cooldowns = IBackendCooldowns.getByUUID(playerUuid);
		if (cooldowns == null) {
			return null;
		}

		String cooldownId = GymCooldown.loss(getId());
		long remainingTime = cooldowns.getRemaining(cooldownId);
		if (remainingTime > 0L) {
			return new CooldownInfo(cooldownId, "lose", remainingTime);
		}

		cooldownId = GymCooldown.win(getId());
		remainingTime = cooldowns.getRemaining(cooldownId);
		if (remainingTime > 0L) {
			return new CooldownInfo(cooldownId, "win", remainingTime);
		}

		return null;
	}

	// TODO: TO BE REMOVED, after implementing the mailbox system
	private boolean enoughFreeSpaceCheck(UUID playerUuid) {
		User user = User.unsafeGetByUUID(playerUuid);
		if (user == null) {
			Logger.error(new MessageBuilder("{uuid} requested a gym inventory free space check but is not locally online")
					.parse("uuid", playerUuid));
			return false;
		}

		int freeSlots = InventoryUtils.getFreeSlots(user);

		IUserGymRecord userGymRecord = IUserGymRecord.get(playerUuid);
		GymRecord gymRecord = userGymRecord.getGymRecord(this.getId());

		if (gymRecord == null || gymRecord.wins() < 1) {
			Logger.debug("(1)free space needed: " + freeSpaceRequiredFirstWin + ", actual free space: " + freeSlots);
			return freeSlots >= freeSpaceRequiredFirstWin;
		}

		Logger.debug("(2)free space needed: " + freeSpaceRequiredNormalWin + ", actual free space: " + freeSlots);
		return freeSlots >= freeSpaceRequiredNormalWin;
	}

	public boolean requiredGymsCheck(UUID playerUuid) {
		if (this.getRequiredGyms() == null) {
			return true;
		}

		IUserGymRecord userGymRecord = IUserGymRecord.get(playerUuid);

		for (String requiredGym : requiredGyms) {
			GymRecord gymRecord = userGymRecord.getGymRecord(requiredGym);
			if (gymRecord == null || gymRecord.wins() < 1) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "Gym{" +
				"name='" + name + '\'' +
				", trainerInfo=" + trainerInfo +
				", trainer=" + trainer +
				'}';
	}

	public static GymBuilder builder() {
		return new GymBuilder();
	}

	public static class GymBuilder {

		private String name = "FORGOT TO ADD NAME";
		private String id = "FORGOT TO ADD ID";
		private MessageBuilderList description = new MessageBuilderList(List.of("FORGOT TO ADD DESCRIPTION"));
		private List<String> requiredGyms = null;
		private List<String> gymClausesAsString = List.of("");
		private Location spawnLocation = Location.of(0, 0, 0);
		private List<String> firstTimeBonusRewards = List.of();
		private List<String> regularRewardCommands = List.of();
		private TrainerInfo trainerInfo = null;
		private ItemStack displayItem = new ItemStack(Items.GREEN_WOOL, 1);
		private CustomModelData customModelData = null;
		private ItemStack badgeItem = new ItemStack(Items.GREEN_WOOL, 1);
		private CustomModelData activeBadgeData = null;
		private CustomModelData inactiveBadgeData = null;
		private MessageBuilder playerWonBattleMessage = new MessageBuilder("Congratulations {user}! You won against {trainer}");
		private MessageBuilder playerLostBattleMessage = new MessageBuilder("You lost versus {trainer}, good luck next time!");
		private Time lossCooldown = Time.minutes(10);
		private Time winCooldown = Time.days(1);
		private int freeSpaceRequiredFirstWin = 0;
		private int freeSpaceRequiredNormalWin = 0;
		private List<String> firstWinPlayerMessage = List.of("");
		private List<String> regularWinPlayerMessage = List.of("");

		GymBuilder() {
		}

		public GymBuilder firstWinPlayerMessage(List<String> firstWinPlayerMessage) {
			this.firstWinPlayerMessage = firstWinPlayerMessage;
			return this;
		}

		public GymBuilder regularWinPlayerMessage(List<String> regularWinPlayerMessage) {
			this.regularWinPlayerMessage = regularWinPlayerMessage;
			return this;
		}

		public GymBuilder name(String name) {
			this.name = name;
			return this;
		}

		public GymBuilder id(String id) {
			this.id = id;
			return this;
		}

		public GymBuilder description(MessageBuilderList description) {
			this.description = description;
			return this;
		}

		public GymBuilder requiredGyms(List<String> requiredGyms) {
			this.requiredGyms = requiredGyms;
			return this;
		}

		public GymBuilder gymClausesAsString(List<String> gymClausesAsString) {
			this.gymClausesAsString = gymClausesAsString;
			return this;
		}

		public GymBuilder spawnLocation(Location spawnLocation) {
			this.spawnLocation = spawnLocation;
			return this;
		}

		public GymBuilder firstTimeBonusRewards(List<String> firstTimeBonusRewards) {
			this.firstTimeBonusRewards = firstTimeBonusRewards;
			return this;
		}

		public GymBuilder regularRewardCommands(List<String> regularRewardCommands) {
			this.regularRewardCommands = regularRewardCommands;
			return this;
		}

		public GymBuilder trainerInfo(TrainerInfo trainerInfo) {
			this.trainerInfo = trainerInfo;
			return this;
		}

		public GymBuilder displayItem(ItemStack displayItem) {
			this.displayItem = displayItem;
			return this;
		}

		public GymBuilder customModelData(CustomModelData customModelData) {
			this.customModelData = customModelData;
			return this;
		}

		public GymBuilder badgeItem(ItemStack badgeItem) {
			this.badgeItem = badgeItem;
			return this;
		}

		public GymBuilder activeBadgeData(CustomModelData activeBadgeData) {
			this.activeBadgeData = activeBadgeData;
			return this;
		}

		public GymBuilder inactiveBadgeData(CustomModelData inactiveBadgeData) {
			this.inactiveBadgeData = inactiveBadgeData;
			return this;
		}

		public GymBuilder playerWonBattleMessage(MessageBuilder playerWonBattleMessage) {
			this.playerWonBattleMessage = playerWonBattleMessage;
			return this;
		}

		public GymBuilder playerLostBattleMessage(MessageBuilder playerLostBattleMessage) {
			this.playerLostBattleMessage = playerLostBattleMessage;
			return this;
		}

		public GymBuilder lossCooldown(Time lossCooldown) {
			this.lossCooldown = lossCooldown;
			return this;
		}

		public GymBuilder winCooldown(Time winCooldown) {
			this.winCooldown = winCooldown;
			return this;
		}

		public GymBuilder freeSpaceRequiredFirstWin(int freeSpaceRequiredFirstWin) {
			this.freeSpaceRequiredFirstWin = freeSpaceRequiredFirstWin;
			return this;
		}

		public GymBuilder freeSpaceRequiredNormalWin(int freeSpaceRequiredNormalWin) {
			this.freeSpaceRequiredNormalWin = freeSpaceRequiredNormalWin;
			return this;
		}

		public Gym build() {
			return new Gym(name, id, description, requiredGyms, gymClausesAsString, spawnLocation,
					firstTimeBonusRewards, regularRewardCommands, trainerInfo, displayItem, customModelData,
					badgeItem, activeBadgeData, inactiveBadgeData, firstWinPlayerMessage, regularWinPlayerMessage,
					playerWonBattleMessage, playerLostBattleMessage, lossCooldown,
					winCooldown, freeSpaceRequiredFirstWin, freeSpaceRequiredNormalWin);
		}

		@Override
		public String toString() {
			return "Gym.GymBuilder(name=" + this.name + ", description=" + this.description +
					", requiredGymToUnlock=" + this.requiredGyms + ", gymClausesAsString=" +
					this.gymClausesAsString + ", spawnLocation=" + this.spawnLocation +
					", firstTimeBonusRewards=" + this.firstTimeBonusRewards + ", regularRewardCommands=" +
					this.regularRewardCommands + ", trainerInfo=" + this.trainerInfo + ", displayItem=" +
					this.displayItem + ", customModelData=" + this.customModelData + ", badgeItem=" +
					this.badgeItem + ", activeBadgeData=" + this.activeBadgeData + ", inactiveBadgeData=" +
					this.inactiveBadgeData + ", playerWonBattleMessage=" + this.playerWonBattleMessage +
					", playerLostBattleMessage=" + this.playerLostBattleMessage + ", lossCooldown=" +
					this.lossCooldown + ", winCooldown=" + this.winCooldown +
					", freeSpaceRequiredFirstWin=" + this.freeSpaceRequiredFirstWin +
					", freeSpaceRequiredNormalWin=" + this.freeSpaceRequiredNormalWin + ")";
		}
	}


}