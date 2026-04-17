package gg.mmorealms.module.plushies.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.plushies.backend.common.command.PlushieCommand;
import gg.mmorealms.module.plushies.backend.common.utils.PlushieUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"spawn"}, parent = PlushieCommand.class, arguments = {"scale", "properties..."}, onlyFor = Command.OnlyFor.PLAYERS)
public class SpawnCommand extends UserCommand {

	public SpawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String scaleString = arguments.getFirst();
		String propertiesString = String.join(" ", arguments.subList(1, arguments.size()));

		float scale;

		try {
			scale = Float.parseFloat(scaleString);
		} catch (NumberFormatException e) {
			user.sendMessage("Number is not valid"); // TODO Config
			return;
		}

		IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().fromProperties(propertiesString);
		pokemon.setScale(scale);

		PlushieUtils.spawnPlushie(user.getPlayer(), user.getPlayer().position(), pokemon);
		user.sendMessage("Plushie spawned"); // TODO Config
	}
}
