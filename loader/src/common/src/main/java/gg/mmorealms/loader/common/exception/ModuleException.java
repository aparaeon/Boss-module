package gg.mmorealms.loader.common.exception;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.CommonModule;

public class ModuleException extends Exception {

	public ModuleException(CommonModule module, String error) {
		this(module, error, null);
	}

	public ModuleException(CommonModule module, String error, Exception cause) {
		super(new MessageBuilder("Exception in module {module}. Error: {error}")
				.parse("module", module.toString())
				.parse("error", error)
				.parse(), cause);
	}
}
