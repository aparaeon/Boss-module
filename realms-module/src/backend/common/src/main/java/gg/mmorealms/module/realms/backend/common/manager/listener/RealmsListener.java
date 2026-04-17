package gg.mmorealms.module.realms.backend.common.manager.listener;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.ShutdownEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerEditBookEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.core.common.dto.event.user.UserTeleportEvent;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.pokemon.backend.common.dto.event.PokemonSpawnEvent;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.ChunkLocation;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.RealmType;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.event.CreateRealmRequest;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsLoader;
import gg.mmorealms.module.realms.backend.common.manager.RealmsManager;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.LoadRealmEvent;
import gg.mmorealms.module.realms.common.dto.event.MultipleRealmStateChangeEvent;
import gg.mmorealms.module.realms.common.dto.event.RealmStateChangeEvent;
import lombok.SneakyThrows;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// TODO move some of the login to RealmManager
@OnlyOn(servers = {ServerType.REALMS})
public class RealmsListener {

	private static final int PLAYER_CHECKS_PER_TICK = 5;

	private @Inject RealmsManager realmsManager;
	private @Inject RealmsConfig config;
	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject MinecraftServer server;
	private @Inject RealmsLoader realmsLoader;

	public RealmsListener() {
		ServerTickEvent.runOnTimer(this::checkPlayersLocation, 5 * 20);
	}

	private void checkPlayersLocation() {
		ServerTickEvent.runOnMultipleTicks(0, this.server.getPlayerList().getPlayerCount() / PLAYER_CHECKS_PER_TICK + 1, this::checkPlayersLocations);
	}

	private void checkPlayersLocations(int tick) {
		for (int i = tick * PLAYER_CHECKS_PER_TICK; i < (tick + 1) * PLAYER_CHECKS_PER_TICK; i++) {
			if (i >= this.server.getPlayerList().getPlayerCount()) {
				break;
			}
			ServerPlayer player = this.server.getPlayerList().getPlayers().get(i);
			User user = User.get(player);
			Location location = user.getLocation();

			IRealm realm = IRealm.getAtLocation(location);

			if (realm == null) {
				realm = IRealm.getByOwner(user);
				user.sendMessage("<red>Please remain in the constraints of your realm. (1)");

				if (realm == null) {
					user.send(IServerLocation.of(ServerType.SPAWN));
				} else {
					realm.send(user);
				}

				continue;
			}

			if (realm.isChunkUnlocked(ChunkLocation.convert(location))) {
				continue;
			}

			user.sendMessage("<red>Please remain in the constraints of your realm. (2)");
			user.teleport(realm.getSpawnLocation());
		}
	}

	@EventHandler
	private void onLoadRealmWorldRequest(LoadRealmEvent event) {
		IUser requester = IUser.getByUUID(event.getRequesterUUID());
		Logger.debug(new MessageBuilder("Loading realm world for {uuid}...")
				.parse("uuid", event.getOwnerUUID())
		);
		Realm realm = RealmsBackendModule.instance().getRealmsLoader().loadObject(event.getOwnerUUID());

		if (realm == null) {
			if (event.getOwnerUUID().equals(event.getRequesterUUID())) {
				requester.sendMessage(config.lang.playerHasNoOwnRealm);
			} else {
				requester.sendMessage(config.lang.playerHasNoRealm);
			}
			Logger.debug("There is no realm world for " + event.getOwnerUUID());

			new RealmStateChangeEvent(event.getOwnerUUID(), null).send();
			return;
		}

		realm.load(requester);
	}

	@EventHandler
	private void onCreateRealmRequest(CreateRealmRequest event) {
		RegionLocation rootLocation = realmsManager.getNextAllocation();
		IRealm realmPrimitive = IRealm.getByOwner(event.getOwnerUUID());

		if (realmPrimitive != null) {
			event.setResult(realmPrimitive.getOwnerUUID());
			return;
		}

		Realm realm = new Realm(event.getRealmType(), event.getOwnerUUID(), rootLocation);
		realm.setState(RealmState.LOADING);
		placeRegions(event.getRealmType(), rootLocation);
		realm.onWorldFilesLoaded();

		realm.setState(RealmState.LOADED);
		event.setResult(realm.getOwnerUUID());
	}

	@SneakyThrows(value = {IOException.class})
	@EventHandler(ignore = true)
	public void placeRegions(RealmType type, RegionLocation location) {
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 2; z++) {
				String from = new MessageBuilder("{home}/data/realms/{type}/r.{x}.{z}.mca")
						.parse("home", System.getProperty("user.dir"))
						.parse("type", type.getProperties().getDiskLocation())
						.parse("x", x)
						.parse("z", z)
						.parse();
				String to = new MessageBuilder("{home}/world/region/r.{x}.{z}.mca")
						.parse("home", System.getProperty("user.dir"))
						.parse("x", location.getX() + x)
						.parse("z", location.getZ() + z)
						.parse();

				Logger.debug(new MessageBuilder("Copying {from} -> {to}")
						.parse("from", from)
						.parse("to", to)
				);
				Files.copy(
						Path.of(from),
						Path.of(to),
						StandardCopyOption.REPLACE_EXISTING
				);
			}
		}
	}

	/**
	 * Set the world border for the player when they teleport to a realm
	 */
	@EventHandler(order = 1000)
	private void onUserTeleport(UserTeleportEvent event) {
		Realm realm = IRealm.getAtLocation(event.getLocation().toLocation());

		if (realm == null) {
			return;
		}

		ServerPlayer player = RealmsBackendModule.instance().getServer().getPlayerList().getPlayer(event.getUuid());
		realm.onPlayerJoin(player);
	}

	@EventHandler
	private void onPlayerEditBookEvent(PlayerEditBookEvent event) {
		User user = IUser.getByPlayer(event.getPlayer());

		ItemStack stack = event.getPlayer().getInventory().getItem(event.getItemIndex());
		ItemStack compass = CodecUtils.deserialize(ItemStack.CODEC, config.visitorMessageItem, CodecUtils.CodecErrorProcessor.ofNull());

		if (!stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString()
				.equals(compass.getComponents().get(DataComponents.CUSTOM_NAME).getString())
				|| !stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 0)
				.equals(compass.get(DataComponents.MAX_STACK_SIZE))) {
			return;
		}

		IRealm currentRealm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_CHANGE_VISITOR_MESSAGE);
		if (currentRealm == null) {
			return;
		}

		WritableBookContent writableBookContent = stack.get(DataComponents.WRITABLE_BOOK_CONTENT);
		if (writableBookContent == null) {
			return;
		}

		if (writableBookContent.pages().size() > config.maxPagesForVisitorMessage) {
			user.sendMessage("<red>The maximum number of pages is " + config.maxPagesForVisitorMessage);
			return;
		}

		if (writableBookContent.pages().getFirst().raw().lines().toList().size() > config.maxLinesForVisitorMessage) {
			user.sendMessage("<red>The maximum number of lines is " + config.maxLinesForVisitorMessage);
			return;
		}

		StringBuilder fullMessage = new StringBuilder();

		for (Filterable<String> page : writableBookContent.pages()) {
			fullMessage.append(page.raw()).append("\n");
		}

		fullMessage.deleteCharAt(fullMessage.length() - 1);

		user.sendMessage("<green>Updated visitor message:");
		String string = fullMessage.toString();
		user.sendMessage("<light_purple>──➤ " + miniMessageManager.sanitize(string));

		currentRealm.getSettings().setVisitorMessage(string);
		event.getPlayer().getInventory().removeItem(stack);
	}

	// TODO: Might need to move this to fabric only and make an alternative for pixelmon
	@EventHandler
	public void onPokemonSpawn(PokemonSpawnEvent event) {
		Location location = LocationUtils.blockPosToLocation(event.getEntity().blockPosition());
		IRealm currentRealm = RealmsUtils.getCurrentRealm(location);

		if (currentRealm == null) {
			return;
		}

		if (!currentRealm.getSettings().isPokemonSpawning()) {
			event.setResult(false);
			return;
		}
	}

	@EventHandler
	public void onPokemonSpawn(ShutdownEvent event) {
		new MultipleRealmStateChangeEvent(RealmState.CRASHED).send();
	}
}