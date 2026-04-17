package gg.mmorealms.module.moderation.velocity.command.punishemnt;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.core.common.dto.PagedMessageGUIConfig;
import gg.mmorealms.module.core.common.gui.PagedMessageGUI;
import gg.mmorealms.module.moderation.velocity.database.GenericUserPunishment;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"history"}, onlyFor = Command.OnlyFor.PLAYERS, arguments = {"target", "page"})
public class HistoryCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public HistoryCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return switch (arguments.size()) {
			case 1 -> recommendPlayersList();
			case 2 -> List.of("1", "2", "3", "4", "5");
			default -> List.of();
		};
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetUsernameOrUUID = arguments.get(0);
		String page = arguments.get(1);

		UserPunishments userPunishments = UserPunishments.getByUsernameOrUUID(targetUsernameOrUUID);

		if (userPunishments == null) {
			sendMessage(player, "User not found.");
			return;
		}

		List<String> template = new ArrayList<>(
				new MessageBuilderList(List.of(
						"Total Bans: {bans}",
						"Total Mutes: {mutes}",
						"Total Warns: {warns}",
						""
				)).parse("user", userPunishments.getUsername())
						.parse("bans", userPunishments.getBans().size())
						.parse("mutes", userPunishments.getMutes().size())
						.parse("warns", userPunishments.getWarns().size())
						.parse()
		);

		for (GenericUserPunishment punishment : userPunishments.getPunishments()) {
			template.add(punishment.toShortString());
		}

		new PagedMessageGUI(
				template,
				PagedMessageGUIConfig.builder(new MessageBuilder("{user}'s History")
								.parse("user", userPunishments.getUsername())
								.parse()
						)
						.numberedList(false)
						.tableColor("<white>")
						.accentColor("<aqua>")
						.build(),
				"history " + targetUsernameOrUUID
		).send(player, page);
	}

}
