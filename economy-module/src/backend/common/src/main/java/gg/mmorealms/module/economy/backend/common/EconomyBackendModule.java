package gg.mmorealms.module.economy.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.economy.backend.common.config.EconomyConfig;
import gg.mmorealms.module.economy.backend.common.manager.BalanceLoader;
import gg.mmorealms.module.economy.common.EconomyCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public class EconomyBackendModule extends EconomyCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static EconomyBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;
	private EconomyConfig config; // exported

	private BalanceLoader balanceLoader;

	public EconomyBackendModule() {
		EconomyBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(EconomyConfig.class));

		this.balanceLoader = new BalanceLoader();
	}

	@Override
	public void onEnable() {
	}
}
