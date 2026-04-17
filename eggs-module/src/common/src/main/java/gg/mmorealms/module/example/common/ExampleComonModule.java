package gg.mmorealms.module.example.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class ExampleComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static ExampleComonModule instance;

	public ExampleComonModule() {
		ExampleComonModule.instance = this;
	}
}