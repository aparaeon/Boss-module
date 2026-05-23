package gg.mmorealms.loader.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.backend.common.dto.event.fabric.CropStompEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerJoinEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerLeaveEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerSetTimeEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.loader.common.dto.event.impl.UserPreLeaveRequest;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class Listener {

	private static final int MAX_EXECUTORS_PER_TICK = 5;
	private static final int BEHIND_ALERT = 100;

	@EventHandler
	private void onUserPreLeaveEvent(UserPreLeaveRequest event) {
		Logger.debug("Received pre-leave request for player: " + event.getUuid());
		ServerPlayer player = BackendLoader.instance().getServer().getPlayerList().getPlayer(event.getUuid());

		if (player == null) {
			Logger.error("Tried to leave player (" + event.getUuid() + ") that is not online");
			return;
		}

		BackendPlayerDependentDatabaseLoader.left(player);
		event.setResult(true);
	}

	@EventHandler
	private void onPlayerLeaveEvent(PlayerLeaveEvent event) {
		BackendPlayerDependentDatabaseLoader.left(event.getPlayer());
	}

	@EventHandler
	private void onPlayerJoinEvent(PlayerJoinEvent event) {
		BackendPlayerDependentDatabaseLoader.joined(event.getPlayer());
	}

	@EventHandler
	private void onServerTickEvent(ServerTickEvent event) {
		List<ServerTickEvent.TickTask<?>> lambdaExecutors = ServerTickEvent.popExecutors(MAX_EXECUTORS_PER_TICK);

		for (ServerTickEvent.TickTask<?> task : lambdaExecutors) {
			if (task == null) {
				continue;
			}
			task.complete();
		}

		int tasksBehind = ServerTickEvent.getExecutorCount();

		if (tasksBehind >= BEHIND_ALERT) {
			int ticksBehind = tasksBehind / MAX_EXECUTORS_PER_TICK + (tasksBehind % MAX_EXECUTORS_PER_TICK == 0 ? 0 : 1);
			int secondsBehind = ticksBehind / 20;

			Logger.warn(new MessageBuilder("Server is running behind by {tasks_count} executors - {ticks_count} ticks - {seconds_count}s")
				.parse("tasks_count", tasksBehind)
				.parse("ticks_count", ticksBehind)
				.parse("seconds_count", secondsBehind)
			);
		}
	}

	@EventHandler
	public void onServerTickEventForScheduledTasks(ServerTickEvent event) {
		int count = ServerTickEvent.getSCHEDULE_TICK_EXECUTORS().size();
		int executedThisTickCount = 0;

		for (int i = 0; i < count; i++) {
			ServerTickEvent.TickScheduledTask element = ServerTickEvent.getSCHEDULE_TICK_EXECUTORS().pop();
			boolean result = element.process();

			if (result) {
				executedThisTickCount++;

				if (executedThisTickCount >= MAX_EXECUTORS_PER_TICK) {
					Logger.warn("There are too many schedules tick executors. Some of them will be executed next tick");
					break;
				}
			}

			ServerTickEvent.getSCHEDULE_TICK_EXECUTORS().add(element);
		}
	}

	// Just to keep logger from complaining
	@EventHandler
	private void onPlayerSetTimeEvent(PlayerSetTimeEvent event) {
	}

	// Just to keep logger from complaining
	@EventHandler
	private void onCropStompEvent(CropStompEvent event) {
	}

}