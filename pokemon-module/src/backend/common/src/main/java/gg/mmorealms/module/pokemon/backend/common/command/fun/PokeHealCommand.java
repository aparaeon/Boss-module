package gg.mmorealms.module.pokemon.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.ICooldownCommand;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.command.IPokemonPartyCommand;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"poke_heal", "pokemon_heal", "pokeheal", "pokemonheal"}, onlyFor = Command.OnlyFor.PLAYERS)
public class PokeHealCommand extends UserCommand implements ICooldownCommand, IPokemonPartyCommand {

	@Getter
	private final String COMMAND_COOLDOWN_KEY = "POKE_HEAL_COOLDOWN";

	private @Inject CoreConfig coreConfig;
	private @Inject PokemonConfig pokemonConfig;

	public PokeHealCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		if (isCommandOnCooldown(user)) {
			return;
		}

		for (int index = 0; index < 6; index++) {
			IPokemon pokemon = getPokemonAtIndex(user, index);

			if (pokemon == null) {
				continue;
			}

			pokemon.heal();
		}

		setCommandCooldown(user, pokemonConfig.healCooldown);
		user.sendMessage(pokemonConfig.lang.healed);
	}

}
