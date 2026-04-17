package gg.mmorealms.module.modpack_rewards.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.modpack_rewards.backend.common.config.ModpackRewardsConfig;
import gg.mmorealms.module.modpack_rewards.backend.common.manager.PacketManager;
import gg.mmorealms.module.modpack_rewards.common.ModpackRewardsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class ModpackRewardsBackendModule extends ModpackRewardsCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static ModpackRewardsBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;

	private PacketManager packetManager;

	private ModpackRewardsConfig config; // exported

	public ModpackRewardsBackendModule() {
		instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(ModpackRewardsConfig.class));

		this.packetManager = new PacketManager();
	}

	@Override
	public void onEnable() {

	}
}