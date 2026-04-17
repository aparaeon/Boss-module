package gg.mmorealms.module.auction_house.backend.common.commands.sub.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.commands.sub.SellCommand;
import gg.mmorealms.module.auction_house.backend.common.config.AuctionHouseConfig;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.backend.common.exception.RateLimitException;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryAddedEvent;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.command.IPokemonPartyCommand;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Command(aliases = {"pokemon"}, arguments = {"slot", "price"}, onlyFor = Command.OnlyFor.PLAYERS, parent = SellCommand.class)
public class PokemonCommand extends UserCommand implements IPokemonPartyCommand {

	private @Inject AuctionHouseConfig config;

	public PokemonCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return getPokemonPartyAutoComplete(argument);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String slotString = arguments.get(0);
		String priceString = arguments.get(1);

		Integer index = getSlotIndex(user, slotString);
		if (index == null) {
			return;
		}

		int price;

		try {
			price = Integer.parseInt(priceString);
		} catch (NumberFormatException e) {
			user.sendMessage(config.lang.invalidPrice);
			return;
		}

		if (price <= 0) {
			user.sendMessage(config.lang.invalidPrice);
			return;
		}

		AtomicLong count = new AtomicLong();

		DatabaseManager.instance().getSessionFactory().inSession((session ->
				count.set(session.createQuery("SELECT COUNT(*) FROM auction_house_entries WHERE ownerUUID = :ownerUUID", Long.class)
						.setParameter("ownerUUID", user.getUUID())
						.getSingleResult()))
		);

		if (count.get() >= user.getCountPermission(AuctionHouseBackendModule.AUCTION_HOUSE_MAX_ENTRIES_PERMISSION_BASE, Range.of(0, 100))) { // TODO Config
			user.sendMessage(config.lang.maxEntries);
			return;
		}

		IPokemon pokemon = getPokemonAtIndex(user, index);

		if (pokemon == null) {
			user.sendMessage(config.lang.noPokemon);
			return;
		}

        if (pokemon.isMega()) {
            user.sendMessage(config.lang.noMega);
            return;
        }

		AuctionHouseEntry entry;
		try {
			entry = new AuctionHouseEntry(user, AuctionHouseEntry.Type.POKEMON, pokemon, price);
			entry.save();
		} catch (DatabaseSaveException e) {
			Logger.error(e);
			user.sendMessage("<red>There was an error while trying to create an auction house entry.");
			return;
		} catch (RateLimitException e) {
			Logger.error(e);
			user.sendMessage(e.getMessage());
			return;
		}

		IPokemonParty party = getPokemonParty(user);
		party.setPokemon(index, null);

		AuctionHouseBackendModule.instance().getEntriesManager().addEntryInCache(entry, true);
		user.sendMessage(config.lang.pokemonListed);
	}
}
