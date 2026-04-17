package gg.mmorealms.module.core.backend.common.manager.listener;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import gg.mmorealms.loader.backend.common.dto.ShutdownEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.MinecartTickEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerJoinEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerPlaceMinecartEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerRespawnEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.PlayerAttackBlockEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.event.impl.UserPreJoinRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.manager.EngineManager;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.core.common.dto.event.server.*;
import gg.mmorealms.module.core.common.dto.event.user.IsValidUserRequest;
import gg.mmorealms.module.core.common.dto.event.user.UserTeleportEvent;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Listener {

	public static final HashMap<UUID, List<RedisRequest<?>>> executeOnJoin = new HashMap<>();

	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject EngineManager engineManager;

	@EventHandler(order = -100_000_000)
	public void onShutdownEvent(ShutdownEvent event) {
		CoreBackendModule.instance().setShuttingDown(true);
		new BackendUnregistrationEvent().sendAndGet();
	}

	@EventHandler
	public void onHeartbeat(Heartbeat event) {
		if (CoreBackendModule.instance().isShuttingDown()) {
			event.setResult(false);
			return;
		}

		boolean result = this.engineManager.isRegistered();
		event.setResult(result);
	}

	@EventHandler
	public void onPreJoin(UserPreJoinRequest event) {
		Logger.debug("Received pre-join request for player: " + event.getUuid());
		executeOnJoin.put(event.getUuid(), event.getJoinEvents(CoreBackendModule.instance().getServerID()));
		event.setResult(true);
	}

	@EventHandler
	private void onPlayerListBroadcast(PlayerListBroadcast event) {
		CoreBackendModule.instance().getEngineManager().setPlayersList(event.getPlayerList());
	}

	@EventHandler
	private void onServerListBroadcast(ServerListBroadcast event) {
		CoreBackendModule.instance().getEngineManager().setServersList(event.getServerList());
	}

	@EventHandler(order = 300_000)
	private void executeOnJoinEvents(PlayerJoinEvent event) {
		List<RedisRequest<?>> events = executeOnJoin.getOrDefault(event.getPlayer().getUUID(), null);

		if (events == null) {
			Logger.debug("No events to execute on join for player: " + event.getPlayer().getUUID());
			return;
		}

		Logger.debug("Executing " + events.size() + " events on join for player: " + event.getPlayer().getUUID());
		for (RedisRequest<?> redisRequest : events) {
			redisRequest.fireSync();
		}
	}

	@EventHandler
	private void onUserTeleportEvent(UserTeleportEvent event) {
		ServerPlayer player = CoreBackendModule.instance().getServer().getPlayerList().getPlayer(event.getUuid());
		Location location = event.getLocation().toLocation();

		if (player == null) {
			Logger.error("Tried to teleport a player that is not online");
			return;
		}

		ServerTickEvent.runOnTick(() -> {
			ServerLevel minecraftWorld = LocationUtils.getWorld(location.getWorld());
			player.teleportTo(minecraftWorld, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			player.sendSystemMessage(miniMessageManager.parse(
					CoreBackendModule.instance().getServerType().getJoinMessage()
			));
		});
	}

	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		ServerPlayer player = event.getNewPlayer();
		IUser user = IUser.getByPlayer(player);

		user.send(IServerLocation.of(ServerType.SPAWN));
	}

	@EventHandler
	private void onCommitCacheBroadcast(CommitCacheBroadcast event) {
		for (DatabaseLoader<?, ?, ?> databaseLoader : SyncedDatabaseLoader.getALL()) {
			databaseLoader.getCache().saveCache(event.isBlocking());
		}
	}

	@EventHandler
	private void onPlayerAttackBlockEvent(PlayerAttackBlockEvent event) {
		// nop
		// Here just to not throw warnings
	}

	@EventHandler
	public void onIsValidUserRequest(IsValidUserRequest request) {
		IUser user = IUser.getByUsername(request.getUserName());

		request.setResult(user != null);
	}

	@EventHandler
	public void onPlayerPlaceMinecraftEvent(PlayerPlaceMinecartEvent event) {
		if (!event.getResult()) {
			return;
		}
		UseOnContext context = event.getContext();
		Level level = context.getLevel();
		BlockPos blockPos = context.getClickedPos();

		AABB box = new AABB(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1);

		List<AbstractMinecart> carts = level.getEntitiesOfClass(
				AbstractMinecart.class,
				box
		);

		if (carts.size() > CoreBackendModule.instance().getConfig().maxMinecartsPerBlock) {
			context.getPlayer().sendSystemMessage(miniMessageManager
					.parse(CoreBackendModule.instance().getConfig().lang.minecartStackingPlayerMessage));
			event.setResult(false);
		}
	}

	@EventHandler
	public void onMinecartTick(MinecartTickEvent event) {
		AbstractMinecart minecart = event.getMinecart();
		Level level = minecart.level();
		int x = Mth.floor(minecart.getX());
		int y = Mth.floor(minecart.getY());
		int z = Mth.floor(minecart.getZ());
		if (level.getBlockState(new BlockPos(x, y - 1, z)).is(BlockTags.RAILS)) {
			--y;
		}

		AABB box = new AABB(x, y, z, x + 1, y + 1, z + 1);
		List<AbstractMinecart> carts = level.getEntitiesOfClass(
				AbstractMinecart.class,
				box
		);

		if (carts.size() > CoreBackendModule.instance().getConfig().maxMinecartsPerBlock + 1) {
			event.setResult(false);
		}
	}
}