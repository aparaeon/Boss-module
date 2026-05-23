package gg.mmorealms.module.realms.backend.common.manager.listener;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerDropItemEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerPickupItemEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.world.entity.player.Player;

//TODO: Detele this after merging core protect
@OnlyOn(servers = {ServerType.REALMS})
public class CoreProtectRealmItemTrackingListener {
	@EventHandler(order = Integer.MAX_VALUE)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if(event.getStack().isEmpty()){
			return;
		}

		Player player = event.getPlayer();
		Location playerPosition = LocationUtils.vecToLocation(player.position());
		IRealm realm = RealmsUtils.getCurrentRealm(playerPosition);
		Location location = playerPosition.offset(realm.getCenter().multiply(-1));

		Logger.info(new MessageBuilder("{user}({uuid}) dropped {item} on the realm owned by {owner_user} at {location} from the center of the realm")
				.parse("user", player.getDisplayName().getString())
				.parse("uuid", player.getStringUUID())
				.parse("item", event.getStack().getItem().toString())
				.parse("owner_user", realm.getOwnerUUID())
				.parse("location", location));
	}

	@EventHandler(order = Integer.MAX_VALUE)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		if (event.getResult().isFalse()) {
			return;
		}

		Player player = event.getPlayer();
		Location playerPosition = LocationUtils.vecToLocation(player.position());
		IRealm realm = RealmsUtils.getCurrentRealm(playerPosition);
		Location location = playerPosition.offset(realm.getCenter().multiply(-1));

		Logger.info(new MessageBuilder("{user}({uuid}) picked {item} on the realm owned by {owner_user} at {location} from the center of the realm")
				.parse("user", player.getDisplayName().getString())
				.parse("uuid", player.getStringUUID())
				.parse("item", event.getItemStack().getItem().toString())
				.parse("owner_user", realm.getOwnerUUID())
				.parse("location", location));
	}
}