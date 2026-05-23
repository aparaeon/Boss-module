package gg.mmorealms.module.login_rewards.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.login_rewards.backend.common.gui.DailyGUI;
import gg.mmorealms.module.login_rewards.common.dto.event.OpenDailyGUIEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class LoginRewardsListener {

	@EventHandler
	public void onOpenDailyGUI(OpenDailyGUIEvent event) {
		MinecraftServer server = BackendLoader.instance().getServer();
		ServerPlayer player = server.getPlayerList().getPlayer(event.getPlayerUuid());

		if (player == null) {
			Logger.warn("Could not open daily GUI for " + event.getPlayerUuid() + ": player is not on this backend.");
			return;
		}

		User user = User.get(player);
		new DailyGUI(user, event).open();
	}

}
