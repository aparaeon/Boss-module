package gg.mmorealms.module.core.backend.common.utils;

import com.mojang.datafixers.util.Pair;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.dto.Range;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.LevelType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LocationUtils {

	private static final List<String> AIR_TYPES = List.of(
			"minecraft:air",
			"minecraft:cave_air"
	);

	private static final List<String> SKIPPABLE_BLOCK_IDS = List.of(
			"minecraft:oak_leaves",
			"minecraft:spruce_leaves",
			"minecraft:birch_leaves",
			"minecraft:jungle_leaves",
			"minecraft:acacia_leaves",
			"minecraft:cherry_leaves",
			"minecraft:dark_oak_leaves",
			"minecraft:mangrove_leaves",
			"minecraft:azalea_leaves",
			"minecraft:flowering_azalea_leaves",
			"cobblemon:saccharine_leaves",
			"cobblemon:apricorn_leaves",

			"minecraft:warped_wart_block",
			"minecraft:nether_wart_block",

			"minecraft:acacia_log",
			"minecraft:cherry_log",
			"minecraft:dark_oak_log",
			"minecraft:mangrove_log",

			"minecraft:chorus_plant",
			"minecraft:chorus_flower",

			"minecraft:bedrock"
	);

	public static @NotNull ServerLevel getWorld() {
		return getWorld("overworld");
	}

	@SuppressWarnings({"DataFlowIssue"})
	public static @NotNull ServerLevel getWorld(@Nullable String name) {
		return CoreBackendModule.instance().getServer().getLevel(LevelType.get(name).getResourceKey());
	}

	public static void setWorldBorder(ServerPlayer player, WorldBorder worldBorder) {
		player.connection.send(new ClientboundInitializeBorderPacket(worldBorder));
	}

	public static Location getRandomLocation(String world, Range x, Range y, Range z) {
		return Location.builder(RandomUtils.getRandom(x), RandomUtils.getRandom(y), RandomUtils.getRandom(z))
				.world(world)
				.build();
	}

	public static BlockPos getRandomBlockPos(String world, Range x, Range y, Range z) {
		Location location = LocationUtils.getRandomLocation(world, x, y, z);
		return locationToBlockPos(location);
	}

	public static BlockPos locationToBlockPos(Location location) {
		return new BlockPos((int) location.getX(), (int) location.getY(), (int) location.getZ());
	}

	public static Location blockPosToLocation(BlockPos position) {
		return Location.of(position.getX(), position.getY(), position.getZ());
	}

	public static Location vecToLocation(Vec3 vec) {
		return Location.of(vec.x, vec.y, vec.z);
	}

	public static BlockPos obfuscateBlockPosition(BlockPos pos, Range range) {
		Pair<Double, Double> offset = obfuscateCoordinates(range);
		return pos.offset(offset.getFirst().intValue(), 0, offset.getSecond().intValue());
	}

	public static Location obfuscateLocation(Location location, Range range) {
		Pair<Double, Double> offset = obfuscateCoordinates(range);
		return location.offset(offset.getFirst(), 0, offset.getSecond());
	}

	private static Pair<Double, Double> obfuscateCoordinates(Range range) {
		int offset = RandomUtils.getRandom(range);
		double angle = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;

		double offsetX = Math.round(Math.cos(angle) * offset);
		double offsetZ = Math.round(Math.sin(angle) * offset);

		return Pair.of(offsetX, offsetZ);
	}

	public static boolean setLocationToGround(Location location, List<String> denyList) {
		location.setY(-1000);

		int emptySpace = 0;

		ServerLevel level = getWorld(location.getWorld());
		BlockPos.MutableBlockPos state = new BlockPos.MutableBlockPos();
		state.set(location.getX(), -1000, location.getZ());
		for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {
			state.setY(y);

			String currentBlockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(state).getBlock()).toString();

			if (AIR_TYPES.contains(currentBlockId)) {
				emptySpace++;
				continue;
			}

			if (SKIPPABLE_BLOCK_IDS.contains(currentBlockId)) {
				emptySpace = 0;
				continue;
			}

			if (denyList.contains(currentBlockId)) {
				return false;
			}

			if (emptySpace >= 2) {
				location.setX(Math.floor(location.getX()) + 0.5);
				location.setZ(Math.floor(location.getZ()) + 0.5);
				location.setY(y + 1);
				return true;
			}

		}

		return false;
	}
}
