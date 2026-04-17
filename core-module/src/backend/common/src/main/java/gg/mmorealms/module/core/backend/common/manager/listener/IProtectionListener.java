package gg.mmorealms.module.core.backend.common.manager.listener;

import gg.mmorealms.loader.backend.common.dto.event.fabric.ExplodeEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.EntityDamageEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.*;

public interface IProtectionListener {

	void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event);

	void onPlayerEntityInteractEvent(PlayerEntityInteractEvent event);

	void onPlayerBlockInteractEvent(PlayerBlockInteractEvent event);

	void onPlayerBoatPlaceEvent(PlayerBoatPlaceEvent event);

	void onPlayerDispensableBlockPlaceEvent(PlayerDispensableBlockPlaceEvent event);

	void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event);

	void onPlayerUseItemEvent(PlayerUseItemEvent event);

	void onEntityDamageEvent(EntityDamageEvent event);

	void onPlayerAttackEntityEvent(PlayerAttackEntityEvent event);

	void onExplosionEvent(ExplodeEvent event);

	void onPlayerDropItemEvent(PlayerDropItemEvent event);

	void onPlayerSignInteractEvent(PlayerSignInteractEvent event);

	void onPlayerPotBlockInteractEvent(PlayerPotBlockInteractEvent event);

	void onPlayerAttackBlockEvent(PlayerAttackBlockEvent event);

	void onPlayerInteractArmorStandEvent(PlayerInteractArmorStandEvent event);

	void onPlayerCommandEvent(PlayerCommandEvent event);
}
