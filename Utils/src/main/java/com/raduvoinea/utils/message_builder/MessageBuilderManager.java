package com.raduvoinea.utils.message_builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBuilderManager {

	private static MessageBuilderManager instance;
	private boolean chatColor;

	public MessageBuilderManager(boolean chatColor) {
		instance = this;

		this.chatColor = chatColor;
	}

	public static void init(boolean chatColor) {
		new MessageBuilderManager(chatColor);
	}

	public static MessageBuilderManager instance() {
		if (instance == null) {
			new MessageBuilderManager(false);
		}
		return instance;
	}

}
