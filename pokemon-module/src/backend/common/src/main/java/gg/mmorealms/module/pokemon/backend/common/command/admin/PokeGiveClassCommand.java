package gg.mmorealms.module.pokemon.backend.common.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Command(aliases = "poke_give_class", arguments = {"target", "class", "shiny"})
public class PokeGiveClassCommand extends BackendCommand {

	private @Inject PokemonConfig config;
	private @Inject MinecraftServer server;
	private @Inject RegistryAccess registryAccess;
	private @Inject PokemonPlatformImplementation platformImplementation;

	public PokeGiveClassCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("class")) {
			return Arrays.stream(PokemonClass.values()).map(PokemonClass::toString).toList();
		}

		if (argument.equals("shiny")) {
			return List.of("true", "false");
		}

		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String pokemonClassName = arguments.get(1);
		String shinyString = arguments.get(2);

		IPokemon pokemon = PokemonClass.parse(pokemonClassName, shinyString);

		if (pokemon == null) {
			sendMessage(sender, "Failed to create Pokemon"); // TODO Config
			return;
		}

		User.executeForUser(targetUsername, user -> {
			platformImplementation.getParty(user.getPlayer()).add(pokemon);

			sendMessage(sender, new MessageBuilder("Given pokemon {pokemon} ({pokemonClassName}) to {user} ({uuid})")
					.parse("pokemon", pokemon.getBriefDescription())
					.parse("pokemonClassName", pokemonClassName)
					.parse("user", user.getUsername())
					.parse("uuid", user.getUUID())
					.parse()
			);
		}, () -> {
			sendMessage(sender, "Player not found"); // TODO Config
		});
	}

}
