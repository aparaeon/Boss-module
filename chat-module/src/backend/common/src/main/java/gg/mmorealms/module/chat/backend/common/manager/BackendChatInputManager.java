package gg.mmorealms.module.chat.backend.common.manager;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.chat.CreateChatCaptureEvent;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class BackendChatInputManager {

	private final HashMap<UUID, ChatCapture> chatCaptures = new HashMap<>();
	private static final MessageBuilder messageFormat = new MessageBuilder("<newline><newline><gray><b>{message} or type '<green><b>cancel<reset><gray><b>' to cancel operation.");

	public BackendChatInputManager() {
	}

	public void registerChatCapture(ChatCapture chatCapture) {
		new CreateChatCaptureEvent(chatCapture.getUser().getUUID()).send();

		chatCaptures.put(chatCapture.getUser().getUUID(), chatCapture);
		CoreBackendModule.instance().getGuiManager().closeGUI(chatCapture.getUser());
		chatCapture.sendMessage();
	}

	public void unregisterInputCallback(UUID uuid) {
		chatCaptures.remove(uuid);
	}

	public void provideInput(UUID uuid, String input) {
		ChatCapture chatCapture = this.chatCaptures.remove(uuid);
		if (chatCapture == null) {
			return;
		}

		boolean result = chatCapture.handle(input);

		if(!result){
			registerChatCapture(chatCapture);
		}
	}

	@Getter
	public abstract static class ChatCapture {
		private final User user;
		private final String message;

		public ChatCapture(User user, String message) {
			this.user = user;
			this.message = messageFormat
					.parse("message", message)
					.parse();
		}

		/**
		 * @return true if the input was handled and the chat capture should be unregistered, false otherwise
		 */
		protected boolean handle(String input) {
			if (input.equalsIgnoreCase("cancel")) {
				this.user.sendMessage("Input cancelled."); // TODO Config
				return true;
			}

			boolean result = this.execute(input);

			if (result) {
				return true;
			}

			this.sendErrorMessage();
			return false;
		}

		/**
		 * @return true if the input was handled and the chat capture should be unregistered, false otherwise
		 */
		protected abstract boolean execute(String input);

		protected void sendMessage() {
			this.user.sendMessage(this.message);
		}

		protected void sendErrorMessage() {
			this.user.sendMessage("<red>That is not a valid input. Please try again or type <green>'cancel' <red>to cancel."); // TODO Config
		}
	}


}
