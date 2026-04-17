package gg.mmorealms.module.realms.backend.common.dto.realm;

import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.core.common.dto.event.server.ServerPrettyNameRequest;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.*;
import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import gg.mmorealms.module.realms.backend.common.manager.RealmsLoader;
import gg.mmorealms.module.realms.backend.common.manager.RealmsManager;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.GetRealmStateRequest;
import gg.mmorealms.module.realms.common.dto.event.LoadRealmEvent;
import gg.mmorealms.module.realms.common.dto.event.RealmStateChangeEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO Add realm type
@Entity(name = "realms")
@Getter
@NoArgsConstructor
public class Realm implements IDatabaseEntry<UUID>, IRealm {

	@Id
	@jakarta.validation.constraints.NotNull
	private UUID ownerUUID;

	// Localisation
	private transient @Setter RegionLocation rootLocation;

	@Setter
	@JdbcTypeCode(SqlTypes.JSON)
	private Location spawnOffset;

	@JdbcTypeCode(SqlTypes.JSON)
	private RealmSettings settings = new RealmSettings();

	@JdbcTypeCode(SqlTypes.JSON)
	private final Map<UUID, TrustLevel> members = new HashMap<>();

	@JdbcTypeCode(SqlTypes.JSON)
	private final Set<UUID> bans = new HashSet<>();

	@Setter
	private boolean isLegendaryCaptureShared = true; // TODO Move to settings

	@JdbcTypeCode(SqlTypes.JSON)
	private boolean[][] unlockedChunks;

	@JdbcTypeCode(SqlTypes.JSON)
	@Setter
	private RealmType type;

	private transient ChunkLocation chunkLocation;
	private final transient AtomicBoolean inInIOOperation = new AtomicBoolean(false);

	public Realm(RealmType type, UUID ownerUUID, RegionLocation rootLocation) {
		Logger.debug(new MessageBuilder("Creating realm for user {user} at location {location}")
				.parse("user", ownerUUID)
				.parse("location", rootLocation)
		);

		this.ownerUUID = ownerUUID;
		this.rootLocation = rootLocation;
		this.spawnOffset = type.getProperties().getSpawnOffset();
		this.type = type;
		this.members.put(ownerUUID, TrustLevel.OWNER);

		RealmsBackendModule.instance().getRealmsLoader().cache(this.ownerUUID, this);
	}

	@Override
	public UUID getIdentifier() {
		return ownerUUID;
	}

	@Override
	public void onEvict() {
		// Teleport everyone to spawn as this realm needs to be cleared
		for (ServerPlayer serverPlayer : getPlayersOnRealm()) {
			IUser user = IUser.getByPlayer(serverPlayer);

			user.sendMessage("<red>You are being send back to spawn as the realm you were currently on is unloading!");
			user.send(IServerLocation.of(ServerType.SPAWN));
		}

		this.setState(null);
	}

	@Override
	public void save() {
		try {
			IDatabaseEntry.super.save();
		} catch (DatabaseSaveException exception) {
			Logger.error(exception);
			return;
		}
		saveToS3();
	}

	public void visit(UUID visitorUUID) {
		IUser visitor = IUser.getByUUID(visitorUUID);

		if (this.getRootLocation() == null) {
			if (!this.isMember(visitor.getUUID())) {
				visitor.sendMessage("<red>Realm not loaded yet, please try again later");
				return;
			}

			String serverID = RealmsBackendModule.instance().getEngineManager().getServersList().getLowestUsageServer(ServerType.REALMS);
			if (serverID == null) {
				visitor.sendMessage("<red>No server found, please try again later");
				return;
			}

			new LoadRealmEvent(visitor.getUUID(), this.getOwnerUUID()).send();
			return;
		}

		if (!this.isMember(visitor.getUUID()) && !this.getSettings().isCanAnyoneVisit()) {
			visitor.sendMessage("<red>This realm doesn't allow public visits");
			return;
		}

		if (this.isBanned(visitor.getUUID())) {
			visitor.sendMessage("<red>You are banned from this realm");
			return;
		}

		if (this.inInIOOperation.get()) {
			visitor.sendMessage(RealmsBackendModule.instance().getConfig().lang.realmStillLoading);
			return;
		}

		if (this.getServerID() == null) {
			visitor.sendMessage("<red>Something went wrong while trying to load your realm, please relog.");
			return;
		}

		this.send(visitor);
	}

	@Override
	public IUser getOwner() {
		return IUser.getByUUID(this.ownerUUID);
	}

	@Override
	public void delete() {
		this.getOwner().sendMessage("<yellow>Your realm is being deleted...");
		IDatabaseEntry.super.delete();
		this.setState(null);

		this.getOwner().sendMessage("<green>Your realm has been deleted.");
	}

	@Override
	public Boolean checkPermission(@NotNull UUID uuid, RealmPermission permission) {
		TrustLevel trustLevel = members.get(uuid);
		return trustLevel != null && permission.getLevel().getLevel() <= trustLevel.getLevel();
	}

	@Override
	public Boolean isBanned(UUID uuid) {
		return this.bans.contains(uuid);
	}

	@Override
	public void addBan(UUID uuid) {
		this.bans.add(uuid);
	}

	@Override
	public void removeBan(UUID uuid) {
		this.bans.remove(uuid);
	}

	public List<String> getMemberList() {
		return members.entrySet().stream()
				.sorted(Comparator.comparingInt((Map.Entry<UUID, TrustLevel> entry) -> entry.getValue().getLevel())
						.reversed())
				.flatMap(entry -> Stream.of(entry.getKey().toString(), entry.getValue().getDisplayName()))
				.collect(Collectors.toList());
	}

	public TrustLevel getTrustLevel(UUID uuid) {
		return members.get(uuid);
	}

	@Override
	public void addMember(UUID uuid) {
		setMemberTrustLevel(uuid, TrustLevel.MEMBER);
	}

	@Override
	public void removeMember(UUID uuid) {
		this.members.remove(uuid);
	}

	@Override
	public Boolean isMember(UUID uuid) {
		return this.members.containsKey(uuid);
	}

	public void setMemberTrustLevel(UUID uuid, TrustLevel trustLevel) {
		this.members.put(uuid, trustLevel);
	}

	public @NotNull RealmSettings getSettings() {
		if (this.settings == null) {
			this.settings = new RealmSettings();
		}

		return this.settings;
	}

	@Override
	public void setSettings(@NotNull RealmSettings settings) {
		this.settings = settings;

		if (!settings.isPokemonSpawning()) {
			deletePokemonsOnRealm();
		}

		for (ServerPlayer player : getPlayersOnRealm()) {
			applySettings(player);
		}
	}

	@Override
	public SyncedDatabaseLoader<UUID, ?, ?, ?> getLoader() {
		return RealmsBackendModule.instance().getRealmsLoader();
	}

	private void saveToS3() {
		if (this.getState() == RealmState.LOADING) {
			Logger.warn(new MessageBuilder("Attempted to save realm {user} to S3 while it is still loading")
					.parse("user", this.ownerUUID)
			);
			return;
		}

		if (!inInIOOperation.compareAndSet(false, true)) {
			Logger.error(new MessageBuilder("Tried to save realm {user} to S3, but it is already in another IO operation")
					.parse("user", this.ownerUUID)
			);
			return;
		}

		Logger.debug(new MessageBuilder("Saving {user} to S3")
				.parse("user", this.ownerUUID)
		);

		for (WorldFile worldFile : getRegionFiles()) {
			RealmsBackendModule.instance().getS3Manager().upload(worldFile.remotePath, worldFile.localPath());
		}

		for (WorldFile worldFile : getEntitiesFiles()) {
			RealmsBackendModule.instance().getS3Manager().upload(worldFile.remotePath, worldFile.localPath());
		}

		Logger.debug(new MessageBuilder("Saved {user} to S3")
				.parse("user", this.ownerUUID)
		);

		inInIOOperation.set(false);
	}

	private List<WorldFile> getGenericWorldFiles(MessageBuilder remotePathBuilder, MessageBuilder localPathBuilder) {
		List<WorldFile> files = new ArrayList<>();

		for (int offsetX = 0; offsetX < REGIONS_COUNT; offsetX++) {
			for (int offsetZ = 0; offsetZ < REGIONS_COUNT; offsetZ++) {
				String remotePath = remotePathBuilder
						.parse("owner", this.ownerUUID)
						.parse("x", offsetX)
						.parse("z", offsetZ)
						.parse();
				String localPath = localPathBuilder
						.parse("x", this.getRootLocation().getX() + offsetX)
						.parse("z", this.getRootLocation().getZ() + offsetZ)
						.parse();

				files.add(
						new WorldFile(
								remotePath, offsetX, offsetZ,
								Path.of(localPath), this.getRootLocation().getX() + offsetX, this.getRootLocation().getZ() + offsetZ
						)
				);
			}
		}

		return files;
	}

	public List<WorldFile> getEntitiesFiles() {
		return getGenericWorldFiles(
				new MessageBuilder("{owner}/entities/r.{x}.{z}.mca"),
				new MessageBuilder("world/entities/r.{x}.{z}.mca")
		);
	}

	public List<WorldFile> getRegionFiles() {
		return getGenericWorldFiles(
				new MessageBuilder("{owner}/region/r.{x}.{z}.mca"),
				new MessageBuilder("world/region/r.{x}.{z}.mca")
		);
	}

	public List<WorldFile> getOldRegionFiles() {
		return getGenericWorldFiles(
				new MessageBuilder("{owner}/r.{x}.{z}.mca"),
				new MessageBuilder("world/region/r.{x}.{z}.mca")
		);
	}

	public List<ServerPlayer> getPlayersOnRealm() {
		List<ServerPlayer> players = new ArrayList<>();

		for (ServerPlayer player : RealmsBackendModule.instance().getServer().getPlayerList().getPlayers()) {
			User user;
			try {
				user = IUser.getByPlayer(player);
			} catch (RuntimeException e) {
				continue;
			}

			if (this.getCenter().distance2D(user.getLocation()) < 512) { // TODO Document what 512 is
				players.add(player);
			}
		}

		return players;
	}

	public void applySettings(ServerPlayer player) {
		settings.apply(player);
	}

	public record WorldFile(String remotePath, int remoteOffsetX, int remoteOffsetZ,
	                        Path localPath, int localOffsetX, int localOffsetZ) {
	}

	@Override
	public Long getDayTime() {
		Level level = RealmsBackendModule.instance().getServer().getLevel(Level.OVERWORLD);

		if (level == null) {
			return null;
		}

		return level.getDayTime();
	}

	public void deletePokemonsOnRealm() {
		ServerLevel overworld = RealmsBackendModule.instance().getServer().overworld();

		double centerX = getCenter().getX();
		double centerZ = getCenter().getZ();
		double size = RealmsBackendModule.instance().getConfig().levelToWorldBorder.getOrDefault(9, 10);

		double minX = centerX - size / 2;
		double minZ = centerZ - size / 2;
		double maxX = centerX + size / 2;
		double maxZ = centerZ + size / 2;

		AABB realmArea = new AABB(
				minX, overworld.getMinBuildHeight(), minZ,
				maxX, overworld.getMaxBuildHeight(), maxZ
		);

		List<? extends net.minecraft.world.entity.Entity> pokemons = overworld.getEntitiesOfClass(
				PokemonBackendModule.instance().getPlatformImplementation().getNativePokemonEntityClass(),
				realmArea,
				entity -> true
		);

		for (net.minecraft.world.entity.Entity pokemon : pokemons) {
			if (pokemon.getTags().contains("mmorealms:plushie")) {
				continue;
			}
			pokemon.remove(RemovalReason.DISCARDED);
		}
	}

	public boolean isChunkUnlocked(ChunkLocation chunkLocation) {
		chunkLocation = getChunkLocationOffset(chunkLocation);

		if (chunkLocation.getX() < 0 || chunkLocation.getX() >= unlockedChunks.length || chunkLocation.getZ() < 0 || chunkLocation.getZ() >= unlockedChunks[0].length) {
			return false; // Out of bounds
		}

		return unlockedChunks[chunkLocation.getX()][chunkLocation.getZ()];
	}

	@Override
	public void unload() {
		RealmsBackendModule.instance().getRealmsLoader().clearCache(this.getOwnerUUID(), true);
	}

	public void placeBorder(int chunkX, int chunkZ) {
		if (!unlockedChunks[chunkX][chunkZ]) {
			return;
		}

		int x = (int) (this.getRootLocation().toLocation().getX() + chunkX * 16);
		int z = (int) (this.getRootLocation().toLocation().getZ() + chunkZ * 16);

		boolean north = !unlockedChunks[chunkX][chunkZ - 1];
		boolean south = !unlockedChunks[chunkX][chunkZ + 1];
		boolean west = !unlockedChunks[chunkX - 1][chunkZ];
		boolean east = !unlockedChunks[chunkX + 1][chunkZ];

		AtomicBoolean placedAnyBlock = new AtomicBoolean(false);

		ServerTickEvent.runOnMultipleTicks(0, 3, (i) -> {
			for (int offset = i * 4; offset < (i + 1) * 4; offset++) {
				for (int y = 319; y >= -64; y--) {
					boolean result = false;

					if (north) {
						result |= placeBorderBlock(x + offset, y, z - 1);
					}
					if (south) {
						result |= placeBorderBlock(x + offset, y, z + 16);
					}
					if (west) {
						result |= placeBorderBlock(x - 1, y, z + offset);
					}
					if (east) {
						result |= placeBorderBlock(x + 16, y, z + offset);
					}

					if (!result) {
						break;
					}

					placedAnyBlock.set(true);
				}
			}
		}, 10);

		ServerTickEvent.runOnTick(() -> {
			if (placedAnyBlock.get()) {
				placePriceTag(chunkX, chunkZ);
			}
		});
	}

	private void placePriceTag(int chunkX, int chunkZ) {
		if (!unlockedChunks[chunkX][chunkZ]) {
			return;
		}

		if (chunkX <= 1 || chunkZ <= 1 ||
				chunkX >= unlockedChunks.length - 1 || chunkZ >= unlockedChunks[0].length - 1) {
			return;
		}

		int x = (int) (this.getRootLocation().toLocation().getX() + chunkX * 16);
		int z = (int) (this.getRootLocation().toLocation().getZ() + chunkZ * 16);

		boolean north = !unlockedChunks[chunkX][chunkZ - 1];
		boolean south = !unlockedChunks[chunkX][chunkZ + 1];
		boolean west = !unlockedChunks[chunkX - 1][chunkZ];
		boolean east = !unlockedChunks[chunkX + 1][chunkZ];

		if (north) {
			placeBorderPriceTagNorth(Location.of(x + 8, 100, z - 1));
		}
		if (south) {
			placeBorderPriceTagSouth(Location.of(x + 8, 100, z + 16));
		}
		if (west) {
			placeBorderPriceTagEast(Location.of(x - 1, 100, z + 8));
		}
		if (east) {
			placeBorderPriceTagWest(Location.of(x + 16, 100, z + 8));
		}
	}


	public void unlockChunk(ChunkLocation chunkLocation) {
		chunkLocation = getChunkLocationOffset(chunkLocation);

		unlockChunk(chunkLocation.getX(), chunkLocation.getZ());
	}

	public void unlockChunk(int chunkX, int chunkZ) {
		removeBorderBlocks(chunkX, chunkZ);
		removeHologram(chunkX, chunkZ);

		unlockedChunks[chunkX][chunkZ] = true;
		Logger.log(new MessageBuilder("User {user} has unlocked chunk {chunkX} {chunkZ}")
				.parse("user", this.ownerUUID)
				.parse("chunkX", chunkX)
				.parse("chunkZ", chunkZ)
		);

		this.placeBorder(chunkX, chunkZ);
	}

	private void removeBorderBlocks(int chunkX, int chunkZ) {
		int x = (int) (this.getRootLocation().toLocation().getX() + chunkX * 16);
		int z = (int) (this.getRootLocation().toLocation().getZ() + chunkZ * 16);

		boolean north = unlockedChunks[chunkX][chunkZ - 1];
		boolean south = unlockedChunks[chunkX][chunkZ + 1];
		boolean west = unlockedChunks[chunkX - 1][chunkZ];
		boolean east = unlockedChunks[chunkX + 1][chunkZ];

		for (int offset = 0; offset < 16; offset++) {
			for (int y = -64; y < 320; y++) {
				if (north) {
					removeBorderBlock(x + offset, y, z);
				}
				if (south) {
					removeBorderBlock(x + offset, y, z + 15);
				}
				if (west) {
					removeBorderBlock(x, y, z + offset);
				}
				if (east) {
					removeBorderBlock(x + 15, y, z + offset);
				}
			}
		}

	}

	private void removeHologram(int chunkX, int chunkZ) {
		ServerLevel world = LocationUtils.getWorld("world");

		int x = (int) (this.getRootLocation().toLocation().getX() + chunkX * 16);
		int z = (int) (this.getRootLocation().toLocation().getZ() + chunkZ * 16);

		boolean north = unlockedChunks[chunkX][chunkZ - 1];
		boolean south = unlockedChunks[chunkX][chunkZ + 1];
		boolean west = unlockedChunks[chunkX - 1][chunkZ];
		boolean east = unlockedChunks[chunkX + 1][chunkZ];

		List<AABB> aabbs = new ArrayList<>();

		if (north) {
			Vec3 position = new Vec3(x + 8, 0, z - 1);
			aabbs.add(new AABB(position.add(-2, -64, -2), position.add(2, 320, 2)));
		}
		if (south) {
			Vec3 position = new Vec3(x + 8, 0, z + 16);
			aabbs.add(new AABB(position.add(-2, -64, -2), position.add(2, 320, 2)));
		}
		if (west) {
			Vec3 position = new Vec3(x - 1, 0, z + 8);
			aabbs.add(new AABB(position.add(-2, -64, -2), position.add(2, 320, 2)));
		}
		if (east) {
			Vec3 position = new Vec3(x + 16, 0, z + 8);
			aabbs.add(new AABB(position.add(-2, -64, -2), position.add(2, 320, 2)));
		}

		for (AABB aabb : aabbs) {
			List<Display.TextDisplay> entities = world.getEntities(EntityType.TEXT_DISPLAY, aabb, (entity) -> true);
			for (Display.TextDisplay entity : entities) {
				entity.remove(RemovalReason.DISCARDED);
			}
		}
	}

	private boolean placeBorderBlock(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		ServerLevel world = LocationUtils.getWorld("world");
		BlockState blockState = world.getBlockState(pos);

		if (blockState.is(Blocks.GRAY_STAINED_GLASS)) {
			return false;
		}

		if (world.getBlockState(pos).is(Blocks.AIR) ||
				world.getBlockState(pos).is(Blocks.SHORT_GRASS) ||
				world.getBlockState(pos).is(Blocks.PINK_PETALS) ||
				world.getBlockState(pos).is(Blocks.TALL_GRASS)) {
			world.setBlock(
					pos,
					Blocks.GRAY_STAINED_GLASS.defaultBlockState(),
					2
			);
		}

		return true;
	}

	private void removeBorderBlock(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		ServerLevel world = LocationUtils.getWorld("world");

		if (world.getBlockState(pos).is(Blocks.GRAY_STAINED_GLASS)) {
			world.setBlock(
					pos,
					Blocks.AIR.defaultBlockState(),
					2
			);
		}
	}


	private void placeBorderPriceTagNorth(Location location) {
		placeBorderPriceTag(location.offsetNew(0, 0, 1.1), 0, 0);
	}

	private void placeBorderPriceTagWest(Location location) {
		placeBorderPriceTag(location.offsetNew(-0.1, 0, 0), 90, 0);
	}

	private void placeBorderPriceTagSouth(Location location) {
		placeBorderPriceTag(location.offsetNew(0, 0, -0.1), 180, 0);
	}

	private void placeBorderPriceTagEast(Location location) {
		placeBorderPriceTag(location.offsetNew(1.1, 0, 0), 270, 0);
	}

	private void placeBorderPriceTag(Location location, float yRot, float xRot) {
		LocationUtils.setLocationToGround(location, new ArrayList<>());
		location.offset(0, 2, 0);

		ServerLevel world = LocationUtils.getWorld("world");

		Display.TextDisplay hologram = new Display.TextDisplay(
				EntityType.TEXT_DISPLAY,
				world
		);

		RealmsConfig config = RealmsBackendModule.instance().getConfig();
		hologram.setText(RealmsBackendModule.instance().getMiniMessageManager().parse("<gold><b>Unlock this chunk<reset><newline><newline>" + config.realmExpansionPrice.toString() + "<newline><aqua>Click to unlock")); // TODO Config

		hologram.moveTo(
				location.getX(),
				location.getY(),
				location.getZ(),
				yRot,
				xRot
		);
		hologram.addTag("hologram");
		world.addFreshEntity(hologram);
	}

	public void onPlayerJoin(ServerPlayer player) {
	}

	public void onWorldFilesLoaded() {
		ScheduleUtils.runTaskAsync(() -> {
			Logger.debug("Setting up realm " + this.ownerUUID);

			Logger.debug("Placing all borders for realm " + this.ownerUUID);
			this.placeAllBorders();

			Logger.debug("Finished setting up realm " + this.ownerUUID);
		});
	}

	private void placeAllBorders() {
		if (unlockedChunks != null) {
			for (int x = 0; x < 64; x++) {
				for (int z = 0; z < 64; z++) {
					this.placeBorder(x, z);
				}
			}
			return;
		}

		getOwner().sendMessage("<green>We are generating your realm borders. This is a one-time process and may take a few seconds.");

		Logger.debug(new MessageBuilder("Creating unlocked chunks for user {user}")
				.parse("user", this.ownerUUID)
		);
		unlockedChunks = new boolean[32 * REGIONS_COUNT][32 * REGIONS_COUNT];

		for (int x = -5; x < 5; x++) {
			for (int z = -5; z < 5; z++) {
				unlockedChunks[32 + x][32 + z] = true;
			}
		}

		for (int x = 0; x < 64; x++) {
			for (int z = 0; z < 64; z++) {
				this.placeBorder(x, z);
			}
		}
	}

	public ChunkLocation getRootChunkLocation() {
		if (chunkLocation == null) {
			chunkLocation = rootLocation.toChunkLocation();
		}
		return chunkLocation;
	}

	public ChunkLocation getChunkLocationOffset(ChunkLocation offset) {
		return offset.offsetNegativeNew(this.getRootChunkLocation());
	}

	public void load(IUser requester) {
		if (this.inInIOOperation.compareAndSet(false, true)) {
			Logger.warn(new MessageBuilder("Tried to load realm {user}, but it is already in another IO operation")
					.parse("user", this.getOwnerUUID())
			);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException exception) {
				Logger.error(exception);
			}
			load(requester);
			return;
		}

		RealmsConfig config = RealmsBackendModule.instance().getConfig();
		RealmsManager realmsManager = RealmsBackendModule.instance().getRealmsManager();
		RealmsLoader realmsLoader = RealmsBackendModule.instance().getRealmsLoader();

		String serverPrettyName;

		// TODO Remove this block
		{
			String serverID = RealmsBackendModule.instance().getServerID();
			serverPrettyName = new ServerPrettyNameRequest(serverID).sendAndGet();

			if (serverPrettyName == null) {
				Logger.error(new MessageBuilder(
						"There was an error while trying to get server pretty name for server with id {server_id}")
						.parse("server_id", serverID)
				);
				serverPrettyName = serverID;
			}
		}

		requester.sendMessage(config.lang.loadingRealmStart
				.parse("server", serverPrettyName)
				.parse("pre", requester.getUUID().equals(this.getOwnerUUID()) ? "your" : "the")
		);

		RegionLocation rootLocation = realmsManager.getNextAllocation();
		this.setRootLocation(rootLocation);
		RealmsBackendModule.instance().getRealmsLoader().cache(this.getOwnerUUID(), this);

		Logger.log(new MessageBuilder("Placing the realm for {uuid} at {location}")
				.parse("uuid", this.getOwnerUUID())
				.parse("location", rootLocation)
		);

		try {
			realmsManager.downloadRealm(this);
			this.onWorldFilesLoaded();
		} catch (Throwable exception) {
			Logger.error(exception);
			requester.sendMessage(config.lang.realmLoadFailed);
			realmsLoader.clearCache(this.getOwnerUUID(), false);
			new RealmStateChangeEvent(this.getOwnerUUID(), null).send();
			return;
		}

		Logger.log(new MessageBuilder("Loaded realm world for {uuid}")
				.parse("uuid", this.getOwnerUUID())
		);

		requester.sendMessage(config.lang.realmLoaded
				.parse("server_id", serverPrettyName)
				.parse("pre", this.getOwnerUUID().equals(requester.getUUID()) ? "Your" : "The")
		);

		this.inInIOOperation.set(false);
		this.setState(RealmState.LOADED);
	}

}