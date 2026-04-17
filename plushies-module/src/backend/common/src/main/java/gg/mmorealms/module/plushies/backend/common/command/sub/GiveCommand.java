package gg.mmorealms.module.plushies.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.plushies.backend.common.command.PlushieCommand;
import gg.mmorealms.module.plushies.backend.common.utils.PlushieUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(aliases = {"give"}, parent = PlushieCommand.class, arguments = {"player", "properties..."})
public class GiveCommand extends BackendCommand {

	private @Inject MinecraftServer server;
	private @Inject PokemonConfig pokemonConfig;

	public GiveCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return switch (argument) {
			case "player" -> recommendPlayersList();
			case "properties" -> Arrays.stream(PokemonClass.values()).map(Enum::name).toList();
			default -> new ArrayList<>();
		};
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.getFirst();
		String propertiesString = String.join(" ", arguments.subList(1, arguments.size()));

		ServerPlayer player = server.getPlayerList().getPlayerByName(targetUsername);

		if (player == null) {
			sendMessage(sender, "Player not found!"); // TODO Config
			return;
		}

		IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().fromProperties(propertiesString);

		player.getInventory().add(PlushieUtils.createPlushieItemStack(pokemon));

		sendMessage(sender, new MessageBuilder("Given plushie with properties {properties} to {playerName}")
				.parse("properties", propertiesString)
				.parse("playerName", targetUsername)
		);
	}
}
