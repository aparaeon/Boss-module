package gg.mmorealms.module.realms.backend.common.manager.listener;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerSetTimeEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.realms.backend.common.dto.RealmSettings;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.common.dto.event.MarkRealmsAsCrashedEvent;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerPlayer;

@OnlyOn(servers = {ServerType.REALMS})
public class RealmOnlyListener {

	@EventHandler
	private void onPlayerSetTimeEvent(PlayerSetTimeEvent event) {
		ServerPlayer player = event.getPlayer();
		Location location = Location.of(
				player.getX(),
				player.getY(),
				player.getZ()
		);
		IRealm realm = IRealm.getAtLocation(location);
		if (realm == null) {
			return;
		}

		RealmSettings settings = realm.getSettings();

		if (settings.doDaylightCycle()) {
			return;
		}

		event.setResult(new ClientboundSetTimePacket(0, settings.getSavedDayTime(), false));
	}

}
