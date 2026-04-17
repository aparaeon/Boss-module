package gg.mmorealms.module.gyms.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.ITrainerPlatformImplementation;
import gg.mmorealms.module.gyms.backend.common.manager.UserGymRecordLoader;
import gg.mmorealms.module.gyms.common.GymsCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class GymsBackendModule extends GymsCommonModule implements BackendModule {
	@Getter
	@Accessors(fluent = true)
	private static GymsBackendModule instance;

	private @Inject BackendMiniMessageManager miniMessageManager;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;

	private GymsConfig config; // exported

	private UserGymRecordLoader gymRecordLoader;

	private ITrainerPlatformImplementation trainerPlatformImplementation;

	public GymsBackendModule() {
		GymsBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(GymsConfig.class));

		this.trainerPlatformImplementation = this.createTrainerImplementation();

		export(this.trainerPlatformImplementation);
		export(this.trainerPlatformImplementation, ITrainerPlatformImplementation.class);

		gymRecordLoader = new UserGymRecordLoader();
	}

	@Override
	public void onEnable() {
	}

	public abstract ITrainerPlatformImplementation createTrainerImplementation();

}