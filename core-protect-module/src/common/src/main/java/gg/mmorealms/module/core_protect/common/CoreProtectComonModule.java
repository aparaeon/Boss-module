package gg.mmorealms.module.core_protect.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class CoreProtectComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static CoreProtectComonModule instance;

	public CoreProtectComonModule() {
		CoreProtectComonModule.instance = this;
	}
}