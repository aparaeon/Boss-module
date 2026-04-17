package gg.mmorealms.loader.common.exception;

import com.raduvoinea.utils.message_builder.MessageBuilder;

public class ModuleLoadException extends Exception {

	public ModuleLoadException(Object module, String error) {
		this(module, error, null);
	}

	public ModuleLoadException(Object module, String error, Exception cause) {
		super(new MessageBuilder("Failed to load module {module}. Error: {error}")
				.parse("module", module.toString())
				.parse("error", error)
				.parse(), cause);
	}

}
