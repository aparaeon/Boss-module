package gg.mmorealms.module.realms.backend.common.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Pair3;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.core.common.dto.ServerList;
import gg.mmorealms.module.core.common.dto.event.server.ServerPrettyNameRequest;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.dto.RealmType;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.event.CreateRealmRequest;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;
import gg.mmorealms.module.realms.backend.common.exception.RealmLoadException;
import gg.mmorealms.module.realms.backend.common.utils.MCAUtils;
import io.github.ensgijs.nbt.io.CompressionType;
import io.github.ensgijs.nbt.mca.*;
import io.github.ensgijs.nbt.mca.entities.Entity;
import io.github.ensgijs.nbt.mca.io.McaFileHelpers;
import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.tag.ListTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class RealmsManager {

	private final static int REGION_SIZE = 512;
	private final static int BASE_X = 100;
	private final static int BASE_Z = 100;
	private final static int MAX_X = 10000;

	private final RegionLocation nextLocation = new RegionLocation(BASE_X, BASE_Z);

	public void createRealm(@NotNull User user, @NotNull RealmType realmType) {
		IRealm realm = IRealm.getByOwner(user);

		if (realm != null) {
			user.sendMessage("You already have a realm. Use /realm tp to go to your realm"); // TODO Add to config
			return;
		}

		String serverID = getLowestUsageRealmServer();

		if (serverID == null) {
			user.sendMessage("We couldn't find a server to create your realm on. Please try again later"); // TODO Add to config
			return;
		}

		String serverPrettyName = new ServerPrettyNameRequest(serverID).sendAndGet();

		if (serverPrettyName == null) {
			Logger.error(new MessageBuilder(
					"There was an error while trying to get server pretty name for server with id {server_id}")
					.parse("server_id", serverID)
			);
			serverPrettyName = serverID;
		}

		user.sendMessage(
				new MessageBuilder("<green>Creating a {type} realm on server {server}")
						.parse("type", realmType.getProperties().getName())
						.parse("server", serverPrettyName)
		);

		CompletableFuture<UUID> future = new CreateRealmRequest(serverID, user.getUUID(), realmType).send(Time.seconds(30));
		UUID uuid;
		try {
			uuid = future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}

		if (future.state() == CompletableFuture.State.SUCCESS) {
			onRealmCreationSuccess(uuid);
			return;
		}

		onRealmCreationError(user);
	}

	private void onRealmCreationSuccess(UUID ownerUUID) {
		IRealm realm = IRealm.getByOwner(ownerUUID);
		IUser user = IUser.getByUUID(ownerUUID);

		if (realm == null) {
			user.sendMessage("We were not able to locate your realm after creation. Please contact an administrator"); // TODO Add to config
			return;
		}

		user.sendMessage("Your realm has been created!"); // TODO Add to config
		realm.send(user);
	}

	private void onRealmCreationError(@NotNull User user) {
		user.sendMessage("There was an error while creating your realm. Please try again later or contact an administrator"); // TODO Add to config);
	}

	public @Nullable String getLowestUsageRealmServer() {
		return RealmsBackendModule.instance().getEngineManager().getServersList().getList().stream()
				.filter(server -> server.type().equals(ServerType.REALMS))
				.min(Comparator.comparingInt(ServerList.ServerEntry::playerCount))
				.map(ServerList.ServerEntry::serverID)
				.orElse(null);
	}

	private interface ChunkProcessor<Chunk extends ChunkBase> {
		void process(Class<Chunk> chunkClass, Chunk chunk, int chunkOffsetX, int chunkOffsetZ);
	}

	private boolean attemptRealmTypeCheck(Realm realm, Realm.WorldFile worldFile) {
		if (realm.getType() != null) {
			return true;
		}

		int checkChunkX = worldFile.remoteOffsetX() * 31;
		int checkChunkZ = worldFile.remoteOffsetZ() * 31;
		int actualTimestamp = 0;

		try (RandomAccessFile file = new RandomAccessFile(worldFile.localPath().toFile(), "r")) {
			byte[] header = MCAUtils.readHeader(file);

			actualTimestamp = MCAUtils.getChunkTimestamp(header, checkChunkX, checkChunkZ);
		} catch (IOException exception) {
			Logger.error(exception);
		}

		Logger.debug("Actual timestamp: " + actualTimestamp + "(" + DateUtils.formatTimestamp(actualTimestamp * 1000L, "yyyy-MM-dd HH:mm:ss") + ")");

		for (Map.Entry<RealmType, RealmType.Properties> realmTypePropertiesEntry : RealmsBackendModule.instance().getConfig().realmTypeProperties.entrySet()) {
			RealmType.Properties properties = realmTypePropertiesEntry.getValue();

			try (RandomAccessFile file = new RandomAccessFile(properties.getRegionLocation(worldFile.remoteOffsetX(), worldFile.remoteOffsetZ()), "r")) {
				byte[] header = MCAUtils.readHeader(file);

				int checkTimestamp = MCAUtils.getChunkTimestamp(header, checkChunkX, checkChunkZ);

				if (checkTimestamp == actualTimestamp) {
					realm.setType(realmTypePropertiesEntry.getKey());
					Logger.debug("Automatically detected realm type: " + realm.getType());
					return true;
				}
			} catch (IOException exception) {
				Logger.error(exception);
			}
		}

		return false;
	}

	private void attemptFix(Realm realm, Realm.WorldFile worldFile,
	                        RandomAccessFile __target, int targetOffset, int chunkX, int chunkZ) {
		if (!attemptRealmTypeCheck(realm, worldFile)) {
			realm.getOwner().sendMessage("We were not able to determine the type of your realm. Please contact an administrator for manual intervention"); // TODO Add to config
			return;
		}

		RealmType.Properties properties = RealmsBackendModule.instance().getConfig().realmTypeProperties.get(realm.getType());

		MCAUtils.executeOnRandomAccessFile(__target, target -> {
			try (RandomAccessFile source = new RandomAccessFile(properties.getRegionLocation(worldFile.remoteOffsetX(), worldFile.remoteOffsetZ()), "r")) {
				byte[] header = MCAUtils.readHeader(source);

				int offset = MCAUtils.getChunkOffset(header, chunkX, chunkZ);
				int sectorCount = MCAUtils.getChunkSectorCount(header, chunkX, chunkZ);

				if (offset == 0 || sectorCount == 0) {
					System.out.println("Chunk is not present.");
					return;
				}

				MCAUtils.ChunkData sourceChunkData = MCAUtils.getChunkData(source, chunkX, chunkZ);

				target.seek(targetOffset * 4096L);
				target.writeInt(sourceChunkData.getLength());
				target.writeByte(sourceChunkData.getCompressionType());
				target.write(sourceChunkData.getData());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private boolean checkChunks(Realm realm, Realm.WorldFile worldFile) {
		try {
			try (RandomAccessFile file = new RandomAccessFile(worldFile.localPath().toFile(), "rw")) {
				byte[] header = MCAUtils.readHeader(file);

				for (int chunkX = 0; chunkX < 32; chunkX++) {
					for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
						int offset = MCAUtils.getChunkOffset(header, chunkX, chunkZ);
						int sectorCount = MCAUtils.getChunkSectorCount(header, chunkX, chunkZ);

						if (offset == 0 || sectorCount == 0) {
							Logger.warn("Chunk is not present.");
							return false;
						}

						file.seek(offset * 4096L);

						int chunkLength = file.readInt();
						AtomicInteger compressionType = new AtomicInteger();
						compressionType.set(file.readUnsignedByte());

						if (Arrays.stream(CompressionType.values()).map(CompressionType::getID).anyMatch(id -> id == compressionType.get())) {
							continue;
						}

						Logger.warn("Unsupported compression type: " + compressionType);
						Logger.warn("Attempting automatic fix...");
						attemptFix(realm, worldFile, file, offset, chunkX, chunkZ);
						file.seek(offset * 4096L);
						compressionType.set(file.readUnsignedByte());


						if (Arrays.stream(CompressionType.values()).map(CompressionType::getID).noneMatch(id -> id == compressionType.get())) {
							Logger.error("Automatic fix failed. Unsupported compression type: " + compressionType);
							return false;
						}

						Logger.good(new MessageBuilder("Chunk {x} {z} was automatically fixed")
								.parse("x", chunkX)
								.parse("z", chunkZ)
						);
					}
				}
			}
		} catch (IOException exception) {
			Logger.error(exception);
			return false;
		}

		return true;
	}

	private <MCAFile extends McaFileBase<Chunk>, Chunk extends ChunkBase> void processMCAFile(
			Realm realm,
			Class<MCAFile> mcaFileClass, Class<Chunk> chunkClass,
			Realm.WorldFile worldFile, ChunkProcessor<Chunk> processor,
			boolean throwOnFail
	) throws RealmLoadException {
		int offsetX = worldFile.localOffsetX() * REGION_SIZE;
		int offsetZ = worldFile.localOffsetZ() * REGION_SIZE;


		MCAFile mcaFile;
		try {
			mcaFile = McaFileHelpers.readAuto(worldFile.localPath());
		} catch (IOException exception) {
			boolean result = checkChunks(realm, worldFile);

			if (result) {
				processMCAFile(realm, mcaFileClass, chunkClass, worldFile, processor, throwOnFail);
				return;
			}

			if (throwOnFail) {
				throw new RealmLoadException(exception);
			}

			return;
		}

		mcaFile.moveRegion(worldFile.localOffsetX(), worldFile.localOffsetZ(), 1L, true);

		for (int chunkX = 0; chunkX < 32; chunkX++) {
			for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
				Chunk chunk = mcaFile.getChunk(chunkX, chunkZ);

				if (chunk == null) {
					continue;
				}

				processor.process(chunkClass, chunk, offsetX, offsetZ);
			}
		}

		try {
			McaFileHelpers.write(mcaFile, worldFile.localPath());
		} catch (IOException exception) {
			throw new RealmLoadException(exception);
		}
	}


	private <MCAFile extends McaFileBase<Chunk>, Chunk extends ChunkBase> void downloadRealmWorldFiles(
			Class<MCAFile> mcaFileClass, Class<Chunk> chunkClass, Realm realm,
			List<ReturnArgLambda<List<Realm.WorldFile>, Realm>> realmWorldFilesFetcher,
			ChunkProcessor<Chunk> processor, boolean throwOnFail
	) throws RealmLoadException {
		boolean result = false;
		for (ReturnArgLambda<List<Realm.WorldFile>, Realm> realmReginFilesFetcher : realmWorldFilesFetcher) {
			List<Realm.WorldFile> worldFiles = realmReginFilesFetcher.run(realm);

			for (Realm.WorldFile worldFile : worldFiles) {
				result = RealmsBackendModule.instance().getS3Manager().download(worldFile.remotePath(), worldFile.localPath());

				if (!result) {
					Logger.warn("There was an error while downloading the file " + worldFile.remotePath());
					realm.getOwner().sendMessage(new MessageBuilder("<yellow> There was an error while downloading the file {file}.") // TODO Config
							.parse("file", worldFile.remotePath())
					);

					if (throwOnFail) {
						break;
					}

					continue;
				}

				processMCAFile(realm, mcaFileClass, chunkClass, worldFile, processor, throwOnFail);
			}

			if (result) {
				break;
			}
		}

		if (!result && throwOnFail) {
			throw new RealmLoadException("Failed to download realm world files");
		}
	}

	private void unloadChunks(Realm realm) {
		ServerLevel level = LocationUtils.getWorld();

	}

	public void downloadRealm(Realm realm) throws RealmLoadException {
		downloadRealmWorldFiles(
				McaRegionFile.class, TerrainChunk.class,
				realm,
				List.of(
						Realm::getRegionFiles,
						Realm::getOldRegionFiles
				),
				(chunkClass, chunk, chunkOffsetX, chunkOffsetZ) -> {
					relocateTileEntities(chunk, chunkOffsetX, chunkOffsetZ);
					mergeTileEntities(chunk);
				},
				true
		);
		downloadRealmWorldFiles(
				McaEntitiesFile.class, EntitiesChunk.class,
				realm,
				List.of(
						Realm::getEntitiesFiles
				),
				(chunkClass, chunk, chunkOffsetX, chunkOffsetZ) ->
						relocateEntities(chunk, chunkOffsetX, chunkOffsetZ)
				,
				false
		);
	}

	private void relocateTileEntities(TerrainChunk chunk, int offsetX, int offsetZ) {
		for (CompoundTag tileEntity : chunk.getTileEntities()) {
			int regionOffsetX = tileEntity.getInt("x") % REGION_SIZE;
			int regionOffsetZ = tileEntity.getInt("z") % REGION_SIZE;

			tileEntity.putInt("x", regionOffsetX + offsetX);
			tileEntity.putInt("z", regionOffsetZ + offsetZ);
		}
	}

	private void relocateEntities(EntitiesChunk chunk, int offsetX, int offsetZ) {
		ListTag<CompoundTag> entitiesTagList = new ListTag<>(new ArrayList<>());

		for (CompoundTag entity : chunk.getEntitiesTag()) {
			double[] pos = entity.getDoubleTagListAsArray("Pos");
			if (pos != null && pos.length == 3) {
				pos[0] = pos[0] % REGION_SIZE + offsetX;
				pos[2] = pos[2] % REGION_SIZE + offsetZ;
			}

			entity.putDoubleArrayAsTagList("Pos", pos);

			if (entity.containsKey("TileX") && entity.containsKey("TileZ")) {
				int regionOffsetX = entity.getInt("TileX") % REGION_SIZE;
				int regionOffsetZ = entity.getInt("TileZ") % REGION_SIZE;

				entity.putInt("TileX", regionOffsetX + offsetX);
				entity.putInt("TileZ", regionOffsetZ + offsetZ);
			}

			entitiesTagList.add(entity);
		}

		chunk.setEntitiesTag(entitiesTagList);

		for (Entity entity : chunk.getEntities()) {
			entity.setUuid(UUID.randomUUID());
		}
	}

	// TODO remove after a while
	// added on 05.03.2025
	private void mergeTileEntities(TerrainChunk chunk) {
		HashMap<Pair3<Integer, Integer, Integer>, List<CompoundTag>> tileEntities = new HashMap<>();

		for (CompoundTag tileEntity : chunk.getTileEntities()) {
			int regionOffsetX = tileEntity.getInt("x") % REGION_SIZE;
			int regionOffsetY = tileEntity.getInt("y");
			int regionOffsetZ = tileEntity.getInt("z") % REGION_SIZE;

			Pair3<Integer, Integer, Integer> pair = new Pair3<>(regionOffsetX, regionOffsetY, regionOffsetZ);

			if (!tileEntities.containsKey(pair)) {
				tileEntities.put(pair, new ArrayList<>());
			}

			tileEntities.get(pair).add(tileEntity);
		}

		chunk.getTileEntities().clear();

		for (Pair3<Integer, Integer, Integer> regionPositionOffset : tileEntities.keySet()) {
			List<CompoundTag> tags = tileEntities.get(regionPositionOffset);

			if (tags.size() == 1) {
				chunk.getTileEntities().add(tags.getFirst());
				continue;
			}

			if (!tags.getFirst().containsKey("Items")) {
				chunk.getTileEntities().add(tags.getFirst());
				continue;
			}

			CompoundTag targetTag = tags.getFirst();
			//noinspection rawtypes
			ListTag items = targetTag.getListTag("Items");

			for (int i = 1; i < tags.size(); i++) {
				if (!tags.get(i).containsKey("Items")) {
					Logger.warn("Found non-chest tile entity at " + regionPositionOffset);
					Logger.warn(tags.get(i).toString());
					continue;
				}

				ListTag<?> otherItems = tags.get(i).getListTag("Items");

				//noinspection unchecked
				items.addAll(otherItems);
			}

			chunk.getTileEntities().add(targetTag);
		}
	}

	public synchronized RegionLocation getNextAllocation() {
		RegionLocation output = nextLocation.clone();

		int x = nextLocation.getX();
		int z = nextLocation.getZ();

		x += Realm.REGIONS_COUNT * 2;

		if (x >= MAX_X) {
			x = BASE_X;
			z += Realm.REGIONS_COUNT * 2;
		}

		nextLocation.setX(x);
		nextLocation.setZ(z);

		return output;
	}
}
