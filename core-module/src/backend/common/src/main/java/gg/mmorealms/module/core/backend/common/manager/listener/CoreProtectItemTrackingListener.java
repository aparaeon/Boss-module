package gg.mmorealms.module.core.backend.common.manager.listener;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerDropItemEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerPickupItemEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import net.minecraft.world.entity.player.Player;

//TODO: Delete this after merging core protect
@OnlyOn(servers = {ServerType.GYMS, ServerType.SPAWN, ServerType.WILD})
public class CoreProtectItemTrackingListener {

	@EventHandler(order = Integer.MAX_VALUE)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getStack().isEmpty()) {
			return;
		}

		Player player = event.getPlayer();

		Logger.info(new MessageBuilder("{user}({uuid}) dropped {item} at {location} in {dimension}")
				.parse("user", player.getDisplayName().getString())
				.parse("uuid", player.getStringUUID())
				.parse("item", event.getStack().getItem().toString())
				.parse("location", player.blockPosition().getCenter().toString())
				.parse("dimension", player.level().dimension().location().getPath()));
	}

	@EventHandler(order = Integer.MAX_VALUE)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		if (event.getResult().isFalse()) {
			return;
		}

		Player player = event.getPlayer();

		Logger.info(new MessageBuilder("{user}({uuid}) picked {item} at {location} in {dimension}")
				.parse("user", player.getDisplayName().getString())
				.parse("uuid", player.getStringUUID())
				.parse("item", event.getItemStack().getItem().toString())
				.parse("location", player.blockPosition().getCenter().toString())
				.parse("dimension", player.level().dimension().location().getPath()));
	}
}