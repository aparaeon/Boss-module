package gg.mmorealms.module.core.backend.common.manager.listener;


import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.EntityDamageEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ProtectionListener {

	@EventHandler(order = 50)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		Entity source = event.getSource().getEntity();

		if (source == null) {
			event.setResult(true);
			return;
		}

		if (source instanceof ServerPlayer && entity instanceof ServerPlayer) {
			event.setResult(false);
			return;
		}

		event.setResult(true);
	}

}
