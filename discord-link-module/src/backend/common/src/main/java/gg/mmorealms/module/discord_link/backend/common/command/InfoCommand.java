package gg.mmorealms.module.discord_link.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO Look into a way to move this to proxy
@Command(aliases = {"discord_link_info"}, onlyFor = Command.OnlyFor.PLAYERS)
public class InfoCommand extends BackendCommand {

	public InfoCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(@NotNull ServerPlayer player, @NotNull List<String> arguments) {
		sendMessage(player,
				"""
						<aqua><b>How to earn a FREE Shiny Pokemon?<reset>
						<white>1. <gray> Join our discord server by clicking <green><click:open_url:https://discord.gg/mmorealms>here</click>
						<white>2. <gray> Go to the <green>#linking <gray>channel
						<white>3. <gray> Type <green>/discord link <gray>in-game and copy the command that appears on the <green>#linking <gray>channel
						<white>4. <gray> You are now linked! Type <aqua>/kit Discord <gray>to redeem your free shiny.
						"""
		); // TODO Config
	}
}
