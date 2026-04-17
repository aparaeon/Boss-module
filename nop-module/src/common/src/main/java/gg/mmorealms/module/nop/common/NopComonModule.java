package gg.mmorealms.module.nop.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class NopComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static NopComonModule instance;

	public NopComonModule() {
		NopComonModule.instance = this;
	}
}