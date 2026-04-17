package gg.mmorealms.module.catch_combo.backend.neoforge;

import gg.mmorealms.module.catch_combo.CatchComboModuleBuildConstants;
import gg.mmorealms.module.catch_combo.backend.common.CatchComboBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(CatchComboModuleBuildConstants.ID)
public class CatchComboNeoForgeModule extends CatchComboBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static CatchComboNeoForgeModule instance;

	public CatchComboNeoForgeModule() {
		CatchComboNeoForgeModule.instance = this;
	}

}