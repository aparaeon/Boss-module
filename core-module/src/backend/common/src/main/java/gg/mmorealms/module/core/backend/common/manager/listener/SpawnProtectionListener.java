package gg.mmorealms.module.core.backend.common.manager.listener;


import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.ExplodeEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.EntityDamageEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.*;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.dto.CommonPermissions;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

// TODO Add permission nodes for all of these
@OnlyOn(servers = {ServerType.SPAWN})
public class SpawnProtectionListener implements IProtectionListener {

	@Override
	@EventHandler
	public void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerEntityInteractEvent(PlayerEntityInteractEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod() || user.hasPermission(CommonPermissions.INTERACT_ENTITY)) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerBlockInteractEvent(PlayerBlockInteractEvent event) {
		event.setResult(!event.getPlayer().isCrouching());
	}

	@Override
	@EventHandler
	public void onPlayerBoatPlaceEvent(PlayerBoatPlaceEvent event) {
		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerDispensableBlockPlaceEvent(PlayerDispensableBlockPlaceEvent event) {
		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerUseItemEvent(PlayerUseItemEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		ItemStack itemInHand = event.getPlayer().getItemInHand(event.getHand());
		if (itemInHand.has(DataComponents.FOOD)) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerAttackEntityEvent(PlayerAttackEntityEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onExplosionEvent(ExplodeEvent event) {
		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerSignInteractEvent(PlayerSignInteractEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerPotBlockInteractEvent(PlayerPotBlockInteractEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerAttackBlockEvent(PlayerAttackBlockEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	@EventHandler
	public void onPlayerInteractArmorStandEvent(PlayerInteractArmorStandEvent event) {
		IUser user = IUser.getByPlayer(event.getPlayer());

		if (user.isMod()) {
			event.setResult(true);
			return;
		}

		event.setResult(false);
	}

	@Override
	public void onPlayerCommandEvent(PlayerCommandEvent event) {

	}
}
