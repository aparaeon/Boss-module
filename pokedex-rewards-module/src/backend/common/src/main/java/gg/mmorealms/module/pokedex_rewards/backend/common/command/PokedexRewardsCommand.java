package gg.mmorealms.module.pokedex_rewards.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokedex_rewards.backend.common.config.PokedexRewardsConfig;
import gg.mmorealms.module.pokedex_rewards.backend.common.gui.PokedexRewardsRewardsGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Command to open Pokedex Rewards GUI
 */
@Command(aliases = {"pokedex", "pokedexrewards", "dexrewards"}, onlyFor = Command.OnlyFor.PLAYERS)
public class PokedexRewardsCommand extends UserCommand {
	private @Inject PokedexRewardsConfig config;

	public PokedexRewardsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new PokedexRewardsRewardsGUI(user).open();
	}
}
