package gg.mmorealms.module.boss.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.boss.BossModuleBuildConstants;
import gg.mmorealms.loader.common.annotation.Module;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
	id = BossModuleBuildConstants.ID,
	version = BossModuleBuildConstants.VERSION,
	authors = {"Radu Voinea"},
	dependencies = BossModuleBuildConstants.DEPENDENCIES
)
public abstract class BossComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static BossComonModule instance;

	public BossComonModule() {
		BossComonModule.instance = this;
	}
}