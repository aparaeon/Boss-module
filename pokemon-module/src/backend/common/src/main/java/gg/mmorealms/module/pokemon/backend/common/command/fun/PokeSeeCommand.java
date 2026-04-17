package gg.mmorealms.module.pokemon.backend.common.command.fun;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.gui.PokeSeeGUI;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"poke_see", "pokesee"}, onlyFor = Command.OnlyFor.PLAYERS, arguments = {"target"})
public class PokeSeeCommand extends UserCommand {

	private @Inject CoreConfig coreConfig;
	private @Inject PokemonConfig pokemonConfig;

	public PokeSeeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String targetUsername = arguments.getFirst();
		IUser target = IUser.getByUsername(targetUsername);

		if (target == null) {
			user.sendMessage("The player does not exist."); // TODO Config
			return;
		}

		if (!target.isOnlineOnNetwork()) {
			user.sendMessage("The player is offline."); // TODO Config
			return;
		}

		new PokeSeeGUI(user, target.getUUID()).open();
	}

}
