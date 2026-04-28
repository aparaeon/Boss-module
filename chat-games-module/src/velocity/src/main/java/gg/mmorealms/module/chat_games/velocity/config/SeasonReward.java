package gg.mmorealms.module.chat_games.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SeasonReward {

	private int fromPlacement = 1;
	private int toPlacement = 1;
	private int requiredSlots = 0;
	private List<String> displayItems = List.of();
	private MessageBuilder announcement = new MessageBuilder(
		"<newline><dark_gray> ▎ <gold><bold>✦ Season End</bold> <dark_gray>— <white><bold>{player}</bold> <gray>finished <gold>#{placement}</gold> with <white>{wins} wins<gray>!<newline>"
	);
	private MessageBuilderList commands = new MessageBuilderList(List.of());
	private List<String> rewardLore = List.of();

	public SeasonReward(int fromPlacement, int toPlacement, MessageBuilder announcement, MessageBuilderList commands) {
		this.fromPlacement = fromPlacement;
		this.toPlacement = toPlacement;
		this.announcement = announcement;
		this.commands = commands;
	}

	public SeasonReward(int fromPlacement, int toPlacement, MessageBuilder announcement, MessageBuilderList commands, List<String> rewardLore) {
		this(fromPlacement, toPlacement, announcement, commands);
		this.rewardLore = rewardLore;
	}

}
