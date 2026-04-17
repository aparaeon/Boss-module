package gg.mmorealms.module.kits.backend.common.exception;

import com.raduvoinea.utils.message_builder.MessageBuilder;

public class ClaimKitException extends Exception {
	public boolean outOfSpace = false;

	public ClaimKitException(String message) {
		super(message);
	}

	public ClaimKitException(String message, boolean outOfSpace) {
		super(message);
		this.outOfSpace = outOfSpace;
	}

	public ClaimKitException(MessageBuilder messageBuilder, boolean outOfSpace) {
		super(messageBuilder.parse());
		this.outOfSpace = outOfSpace;
	}

	public ClaimKitException(MessageBuilder messageBuilder) {
		super(messageBuilder.parse());
	}

}
