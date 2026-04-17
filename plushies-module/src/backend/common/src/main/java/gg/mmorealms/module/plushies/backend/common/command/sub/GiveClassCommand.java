package gg.mmorealms.module.plushies.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.plushies.backend.common.command.PlushieCommand;
import gg.mmorealms.module.plushies.backend.common.utils.PlushieUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(aliases = {"give_class"}, parent = PlushieCommand.class, arguments = {"player", "class", "shiny"})
public class GiveClassCommand extends BackendCommand {

	private @Inject MinecraftServer server;
	private @Inject PokemonConfig cobblemonConfig;

	public GiveClassCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return switch (argument) {
			case "player" -> recommendPlayersList();
			case "class" -> Arrays.stream(PokemonClass.values()).map(Enum::name).toList();
			case "shiny" -> List.of("true", "false");
			default -> new ArrayList<>();
		};
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String className = arguments.get(1);
		String shinyString = arguments.get(2);
		IPokemon pokemon = PokemonClass.parse(className, shinyString);

		if (pokemon == null) {
			sendMessage(sender, "Invalid Pokemon class or shiny value"); // TODO Config
			return;
		}

		ServerPlayer player = server.getPlayerList().getPlayerByName(targetUsername);

		if (player == null) {
			sendMessage(sender, "Player not found"); // TODO Config
			return;
		}

		ItemStack item = PlushieUtils.createPlushieItemStack(pokemon);
		player.getInventory().add(item);

		boolean shiny = Boolean.parseBoolean(shinyString);
		sendMessage(sender, new MessageBuilder("Given plushie {plushieName}{shiny?} ({plushieClassName}) to {playerName}")
				.parse("plushieName", pokemon.getName())
				.parse("shiny?", shiny ? " shiny" : "")
				.parse("plushieClassName", className)
				.parse("playerName", targetUsername)
		); // TODO Config
	}
}
