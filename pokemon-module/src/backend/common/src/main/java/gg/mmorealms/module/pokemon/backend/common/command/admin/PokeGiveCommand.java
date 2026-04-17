package gg.mmorealms.module.pokemon.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"poke_give", "pokemon_give"}, onlyFor = Command.OnlyFor.PLAYERS, arguments = {"party_slot", "user"})
public class PokeGiveCommand extends UserCommand {
	public PokeGiveCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("party_slot")) {
			return List.of("1", "2", "3", "4", "5", "6");
		}

		return super.onAutoComplete(argument, context);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String indexString = arguments.get(0);
		String username = arguments.get(1);

		int index;

		try {
			index = Integer.parseInt(indexString) - 1;
		} catch (NumberFormatException e) {
			user.sendMessage("<red>Invalid userParty slot!");
			return;
		}

		IUser targetUser = IUser.getByUsername(username);
		if (targetUser == null) {
			user.sendMessage("<red>User not found!");
			return;
		}

		IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().getParty(user.getPlayer()).getPokemon(index);
		if (pokemon == null) {
			user.sendMessage("<red>You must have a pokemon in the specified slot to give it to a player!");
			return;
		}

		IPokemonParty userParty = PokemonBackendModule.instance().getPlatformImplementation().getParty(user.getPlayer());
		userParty.setPokemon(index, null);
		IPokemonParty targetParty = PokemonBackendModule.instance().getPlatformImplementation().getParty(targetUser.getUUID());
		targetParty.add(pokemon);

		user.sendMessage(new MessageBuilder("<green>Successfully gave your {pokemon_name} to {user}")
				.parse("pokemon_name", pokemon.getSpeciesName())
				.parse("user", targetUser.getUsername())
		);

		targetUser.sendMessage(new MessageBuilder("<green>You have received {pokemon_name} from {user}")
				.parse("pokemon_name", pokemon.getSpeciesName())
				.parse("user", user.getUsername()));

		Logger.info(new MessageBuilder("{sender} gave {pokemon} to {receiver} using poke_give")
				.parse("sender", user.getUsername())
				.parse("pokemon", pokemon.getBriefDescription())
				.parse("receiver", targetUser.getUsername())
		);
	}
}
