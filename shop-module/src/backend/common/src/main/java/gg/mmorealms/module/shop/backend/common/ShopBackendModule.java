package gg.mmorealms.module.shop.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import gg.mmorealms.module.shop.backend.common.shop.ShopManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class ShopBackendModule extends ShopCommonModule implements BackendModule {
	@Getter
	@Accessors(fluent = true)
	private static ShopBackendModule instance;

	@Getter
	@Accessors(fluent = true)
	private final ShopManager manager;

	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;

	@Setter
	@Accessors(fluent = true)
	private ShopConfig config;

	public ShopBackendModule() {
		ShopBackendModule.instance = this;
		manager = new ShopManager();
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(ShopConfig.class));
	}

	@Override
	public void onEnable() {

	}
}
