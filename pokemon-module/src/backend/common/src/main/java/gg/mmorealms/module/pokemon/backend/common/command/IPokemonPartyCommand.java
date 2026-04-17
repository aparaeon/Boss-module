package gg.mmorealms.module.pokemon.backend.common.command;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface IPokemonPartyCommand {

	default List<String> getPokemonPartyAutoComplete(String argument) {
		if (argument.equals("slot")) {
			return getPokemonPartySlots();
		}

		return Collections.emptyList();
	}

	default List<String> getPokemonPartySlots() {
		return List.of("1", "2", "3", "4", "5", "6");
	}

	@Nullable
	default Integer parseSlot(@NotNull User user, @NotNull String slotString) {
		PokemonConfig config = PokemonBackendModule.instance().getConfig();
		String message = config.lang.invalidSlot;

		try {
			int slot = Integer.parseInt(slotString);

			if (slot < 1 || slot > 6) {
				user.sendMessage(message);
				return null;
			}

			return slot;
		} catch (NumberFormatException e) {
			user.sendMessage(message);
			return null;
		}
	}

	@Nullable
	default Integer getSlotIndex(@NotNull User user, @NotNull String slotString) {
		Integer slot = parseSlot(user, slotString);
		if (slot != null) {
			return slot - 1;
		}

		return null;
	}

	@Nullable
	default IPokemon getPokemonInSlot(@NotNull User user, @NotNull String slotString) {
		Integer index = getSlotIndex(user, slotString);
		if (index != null) {
			return getPokemonAtIndex(user, index);
		}

		return null;
	}

	@Nullable
	default IPokemon getPokemonAtIndex(@NotNull User user, int index) {
		IPokemon pokemon = getPokemonParty(user).getPokemon(index);

		if (pokemon == null) {
			PokemonConfig config = PokemonBackendModule.instance().getConfig();
			MessageBuilder messageBuilder = config.lang.noPokemonInSlot
					.parse("slot", index + 1);

			user.sendMessage(messageBuilder);
		}

		return pokemon;
	}

	default IPokemonParty getPokemonParty(User user) {
		return PokemonBackendModule.instance()
				.getPlatformImplementation()
				.getParty(user.getPlayer());
	}

}
