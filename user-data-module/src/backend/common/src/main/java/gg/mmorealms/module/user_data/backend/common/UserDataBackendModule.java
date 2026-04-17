package gg.mmorealms.module.user_data.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.user_data.backend.common.database.UserData;
import gg.mmorealms.module.user_data.backend.common.manager.BackendUserSettingsLoader;
import gg.mmorealms.module.user_data.common.UserDataCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class UserDataBackendModule extends UserDataCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static UserDataBackendModule instance;

	private @Inject MinecraftServer server;

	private BackendUserSettingsLoader backendUserSettingsLoader;

	public UserDataBackendModule() {
		UserDataBackendModule.instance = this;
	}

	@Override
	public void onInit() {
		backendUserSettingsLoader = new BackendUserSettingsLoader();

		BackendPlayerDependentDatabaseLoader.registerPlayerMethod(
				"User Data",
				(player) -> {
					UserData data = UserData.get(player);
					data.apply(player);
				},
				(player, evictFromCache) -> {
					try {
						new UserData(player, evictFromCache).save();
					} catch (DatabaseSaveException e) {
						Logger.error(e);
					}
				},
				Time.minutes(5)
		);

		UserData.init();
	}

	@Override
	public void onEnable() {

	}

}