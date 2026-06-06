package gg.mmorealms.module.boss.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.boss.BossModuleBuildConstants;
import gg.mmorealms.module.boss.common.BossComonModule;
import gg.mmorealms.module.boss.velocity.config.BossVelocityConfig;
import gg.mmorealms.module.boss.velocity.manager.BossLifecycleManager;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = BossModuleBuildConstants.ID,
		name = BossModuleBuildConstants.ID,
		version = BossModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class BossVelocityModule extends BossComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static BossVelocityModule instance;

	private @Inject ServerManager serverManager;
	private @Inject FileManager fileManager;

	private BossVelocityConfig config;
	private BossLifecycleManager lifecycleManager;

	public BossVelocityModule() {
		BossVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {
		this.config = export(fileManager.load(BossVelocityConfig.class));
		validate(this.config);
		this.lifecycleManager = export(new BossLifecycleManager(this.config, this.serverManager));
	}

	@Override
	public void onEnable() throws ModuleException {
		this.lifecycleManager.start();
	}

	private void validate(BossVelocityConfig cfg) throws ModuleException {
		if (cfg.spawnInterval == null || cfg.spawnInterval.toMilliseconds() <= 0) {
			throw new ModuleException(this,"spawnInterval must be > 0");
		}
		if (cfg.baseSpawnChance < 0) {
			throw new ModuleException(this,"baseSpawnChance must be >= 0");
		}
		if (cfg.tierWeights == null || cfg.tierWeights.isEmpty()) {
			throw new ModuleException(this,"tierWeights must be non-empty");
		}
		int sum = 0;
		for (int w : cfg.tierWeights.values()) {
			if (w < 0) throw new ModuleException(this,"tierWeights values must be >= 0");
			sum += w;
		}
		if (sum <= 0) {
			throw new ModuleException(this,"tierWeights sum must be > 0");
		}
	}
}
