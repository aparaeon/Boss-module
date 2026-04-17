package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SimpleConsole implements CommandSender {

	private final List<String> messages = new ArrayList<>();

	@Override
	public void sendMessage(String message) {
		messages.add(message);
	}

}
