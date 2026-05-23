package gg.mmorealms.module.realms.backend.common.manager.listener;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.CropStompEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.ExplodeEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.EntityDamageEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerCommandEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.manager.listener.IProtectionListener;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.ChunkLocation;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;
import gg.mmorealms.module.realms.backend.common.gui.ChunkUnlockGUI;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;


@OnlyOn(servers = {ServerType.REALMS})
public class ProtectionListener implements IProtectionListener {
	private static final int OVERRIDE_ORDER = 100;

	private final RealmsConfig config = RealmsBackendModule.instance().getConfig();
	private @Inject BackendMiniMessageManager miniMessageManager;

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.BLOCK_BREAK,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerEntityInteractEvent(PlayerEntityInteractEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.ENTITY_INTERACT,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerBlockInteractEvent(PlayerBlockInteractEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.BLOCK_INTERACT,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerBoatPlaceEvent(PlayerBoatPlaceEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.BOAT_PLACE,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerDispensableBlockPlaceEvent(PlayerDispensableBlockPlaceEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.DISPENSABLE_BLOCK_PLACE,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.BLOCK_PLACE,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerUseItemEvent(PlayerUseItemEvent event) {
		event.setResult(checkPerms(
				event.getPlayer(),
				RealmPermission.ITEM_USE_EVENT,
				event.getLocation()
		));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		Entity source = event.getSource().getEntity();

		boolean result = switch (source) {
			case ServerPlayer ignored when entity instanceof ServerPlayer -> false;
			case ServerPlayer player -> checkPerms(
					player,
					RealmPermission.ENTITY_DAMAGE,
					event.getLocation()
			);
			case null, default -> true;
		};

		event.setResult(result);
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerAttackEntityEvent(PlayerAttackEntityEvent event) {
		User user = User.get(event.getPlayer());

		event.setResult(checkPerms(user, RealmPermission.ENTITY_ATTACK, event.getLocation()));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onExplosionEvent(ExplodeEvent event) {
		event.setResult(false);
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		User user = User.get(event.getPlayer());
		event.setResult(checkPerms(user, RealmPermission.ITEM_DROP_EVENT, event.getLocation()));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerSignInteractEvent(PlayerSignInteractEvent event) {
		event.setResult(checkPerms(event.getPlayer(), RealmPermission.SIGN_INTERACT_EVENT, event.getLocation()));
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerPotBlockInteractEvent(PlayerPotBlockInteractEvent event) {
		event.setResult(checkPerms(event.getPlayer(), RealmPermission.DECORATED_POT_INTERACT_EVENT, event.getLocation()));
	}

	@EventHandler(order = OVERRIDE_ORDER)
	public void onCropStompEvent(CropStompEvent event) {
		if (BackendLoader.instance().getServerType() != ServerType.REALMS) {
			event.setResult(false);
			return;
		}
		if (event.getLevel().isClientSide) {
			return;
		}
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}

		IUser user = IUser.getByPlayer(player);
		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.CROP_STOMP);

		if (realm == null) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerInteractArmorStandEvent(PlayerInteractArmorStandEvent event) {
		event.setResult(checkPerms(event.getPlayer(), RealmPermission.ARMOR_STAND_INTERACT_EVENT, event.getLocation()));
	}

	@Override
	public void onPlayerCommandEvent(PlayerCommandEvent playerCommandEvent) {

	}

	@Override
	@EventHandler(order = OVERRIDE_ORDER)
	public void onPlayerAttackBlockEvent(PlayerAttackBlockEvent event) {
		event.setResult(checkPerms(event.getPlayer(), RealmPermission.BLOCK_ATTACK, event.getLocation()));
	}

	@EventHandler(ignore = true)
	private boolean checkPerms(User user, RealmPermission permission, @Nullable Location location) {
		if (location == null) {
			location = user.getBlockLocation();
		}

		Realm realm = IRealm.getAtLocation(location);

		if (realm == null) {
			return false;
		}

		ChunkLocation chunkLocation = ChunkLocation.convert(location);

		if (!realm.isChunkUnlocked(chunkLocation)) {
			new ChunkUnlockGUI(user, realm, chunkLocation).open();
			return false;
		}

		if (user.isMod()) {
			return true;
		}

		return realm.checkPermission(user, permission);
	}

	@EventHandler(ignore = true)
	private boolean checkPerms(ServerPlayer player, RealmPermission permission, @Nullable Location location) {
		return checkPerms(User.get(player), permission, location);
	}
}
