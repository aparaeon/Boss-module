package gg.mmorealms.loader.common.exception;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.LoadedModule;
import gg.mmorealms.loader.common.dto.ModuleID;

public class MissingDependencyException extends Exception {
	public MissingDependencyException(LoadedModule metadata, ModuleID missingDependency) {
		super(new MessageBuilder("Module {module} is missing a dependency: {dependency}")
			.parse("module", metadata.getId())
			.parse("dependency", missingDependency)
			.parse()
		);
	}
}
