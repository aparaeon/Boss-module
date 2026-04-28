package gg.mmorealms.module.chat_games.velocity.config;

import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatGamesReward implements IWeighted {

	private double weight = 1.0;
	private MessageBuilder rewardMessage = new MessageBuilder("<green>You won a reward!");
	private MessageBuilderList commands = new MessageBuilderList(List.of("say Reward placeholder"));
	private List<String> rewardLines = List.of();

	public ChatGamesReward(double weight, MessageBuilder rewardMessage, MessageBuilderList commands) {
		this.weight = weight;
		this.rewardMessage = rewardMessage;
		this.commands = commands;
	}

}
