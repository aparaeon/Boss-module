package gg.mmorealms.module.pokemon.backend.common.command.fun;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.command.IPokemonPartyCommand;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Command(aliases = {"poke_color", "pokecolor"}, onlyFor = Command.OnlyFor.PLAYERS, arguments = {"slot", "text..."})
public class PokeColorCommand extends UserCommand implements IPokemonPartyCommand {

	private static final String RESET = "reset";

	private @Inject PokemonConfig pokemonConfig;

	public PokeColorCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("slot")) {
			return getPokemonPartySlots();
		}
		return Collections.emptyList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IPokemon pokemon = getPokemonInSlot(user, arguments.getFirst());
		if (pokemon == null) {
			return;
		}

		String text = String.join(" ", arguments.subList(1, arguments.size()));

		if (text.equalsIgnoreCase(RESET)) {
			pokemon.resetNameStyle();
			pokemon.setColor("");
			user.sendMessage(pokemonConfig.pokeColor.colorReset);
			return;
		}

		if (text.isBlank()) {
			user.sendMessage(pokemonConfig.pokeColor.invalidColor);
			return;
		}

		pokemon.applyNameStyle(text);
		pokemon.setColor(text);

		user.sendMessage(pokemonConfig.pokeColor.colorApplied);
	}
}