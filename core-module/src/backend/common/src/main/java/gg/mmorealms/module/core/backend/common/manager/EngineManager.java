package gg.mmorealms.module.core.backend.common.manager;


import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.BackendDetails;
import gg.mmorealms.module.core.common.dto.PlayerList;
import gg.mmorealms.module.core.common.dto.ServerList;
import gg.mmorealms.module.core.common.dto.event.server.BackendRegistrationRequest;
import gg.mmorealms.module.core.common.dto.event.server.Heartbeat;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Getter
public class EngineManager {

	private final BackendDetails backendDetails;
	private boolean registered;
	private boolean firstRegistration = true;
	private Thread heartBeatThread;

	private @Setter PlayerList playersList = new PlayerList();
	private @Setter ServerList serversList = new ServerList();

	public EngineManager(BackendDetails backendDetails) {
		Logger.info("Creating EngineManager for backend with details: " + backendDetails);
		this.backendDetails = backendDetails;
		this.registered = false;

		if(CommonLoader.DUMMY_MODE){
			return;
		}

		this.heartBeatThread = new Thread(this::executeHeartBeat);
		this.heartBeatThread.start();
	}

	private void executeHeartBeat() {
		boolean firstRun = true;
		Logger.good("Starting heartbeat thread...");

		while (true) {
			try {
				// Patch to support long timeout for dev environments
				if (!firstRun) {
					//noinspection BusyWait
					Thread.sleep(CommonLoader.instance().getRedisConfig().getTimeout());
				} else {
					Logger.debug("First run, waiting 10 seconds before starting heartbeat...");
					//noinspection BusyWait
					Thread.sleep(10000L);
					firstRun = false;
				}

				if (CoreBackendModule.instance().isShuttingDown()) {
					return;
				}

				if (!isRegistered()) {
					registerEngine();
					continue;
				}

				CompletableFuture<Boolean> activeRequest = new Heartbeat().send();
				Boolean result = activeRequest.get(5, TimeUnit.SECONDS);

				if (activeRequest.state() != Future.State.SUCCESS) {
					Logger.warn("[Timeout] Engine has timed out!");
					markAsUnregistered();
					continue;
				}

				if (result == null || !result) {
					Logger.warn("[Bad-Response] Engine has timed out!");
					markAsUnregistered();
				}
			} catch (Exception e) {
				Logger.error("Error in heartbeat: " + e.getMessage());
			}
		}
	}

	private void markAsRegistered() {
		registered = true;
	}

	private void markAsUnregistered() {
		registered = false;
	}

	private void registerEngine() {
		Logger.debug("Attempting to register server with the engine...");
		CompletableFuture<Boolean> response = new BackendRegistrationRequest(
				this.backendDetails.getHostname(),
				this.backendDetails.getPort(),
				this.backendDetails.getServerType(),
				this.firstRegistration
		).send();
		this.firstRegistration = false;

		boolean result;

		try {
			result = response.get();
		} catch (InterruptedException | ExecutionException e) {
			Logger.error(e);
			Logger.error("[Timeout] Failed to register server with the engine! Please check if the engine is online and if they are both the engine and this server are using the same redis channel.");
			return;
		}

		if (!result) {
			Logger.error("[Bad-Response] Failed to register server with the engine! Please check if the engine is online and if they are both the engine and this server are using the same redis channel.");
			return;
		}

		Logger.good("Server registered with the engine!");
		markAsRegistered();
	}


}