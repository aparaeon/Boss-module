package gg.mmorealms.module.core.backend.common.manager.listener;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.dto.ShutdownEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.ExplodeEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.EntityDamageEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.*;
import gg.mmorealms.loader.common.dto.event.impl.UserPreLeaveRequest;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.event.UserFullyLoadedEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataProtectionListener implements IProtectionListener {

	private final static int PROTECTION_ORDER = 100000;
	private final static Set<UUID> DISABLED_LIST = new HashSet<>();

	private boolean serverShuttingDown = false;

	@EventHandler(order = -PROTECTION_ORDER)
	private void onShutdownEvent(ShutdownEvent event) {
		serverShuttingDown = true;
	}

	private boolean check(UUID uuid) {
		return serverShuttingDown || DISABLED_LIST.contains(uuid);
	}

	@EventHandler(order = -PROTECTION_ORDER)
	private void onUserPreLeaveEvent(UserPreLeaveRequest event) {
		((User) IUser.getByUUID(event.getUuid())).getPlayer().closeContainer();
		DISABLED_LIST.add(event.getUuid());
	}

	@EventHandler(order = PROTECTION_ORDER)
	private void onPlayerJoinEvent(PlayerJoinEvent event) {
		DISABLED_LIST.remove(event.getPlayer().getUUID());
		new UserFullyLoadedEvent(event.getPlayer().getUUID()).send();
	}

	@EventHandler
	private void onPlayerLeaveEvent(PlayerLeaveEvent event) {
		DISABLED_LIST.remove(event.getPlayer().getUUID());
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerEntityInteractEvent(PlayerEntityInteractEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerBlockInteractEvent(PlayerBlockInteractEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerBoatPlaceEvent(PlayerBoatPlaceEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerDispensableBlockPlaceEvent(PlayerDispensableBlockPlaceEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerUseItemEvent(PlayerUseItemEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (this.check(event.getEntity().getUUID())) {
			event.setResult(false);
		}
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerAttackEntityEvent(PlayerAttackEntityEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	public void onExplosionEvent(ExplodeEvent event) {
		// Nothing to do, explosions are not player driven
	}

	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerSignInteractEvent(PlayerSignInteractEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerPotBlockInteractEvent(PlayerPotBlockInteractEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerAttackBlockEvent(PlayerAttackBlockEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerInteractArmorStandEvent(PlayerInteractArmorStandEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			event.setResult(false);
		}
	}

	@Override
	@EventHandler(order = PROTECTION_ORDER)
	public void onPlayerCommandEvent(PlayerCommandEvent event) {
		if (this.check(event.getPlayer().getUUID())) {
			Logger.debug("Blocking command from player " + event.getPlayer().getUUID() + " due to data protection");
			event.setResult(false);
		}
	}
}
