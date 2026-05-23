package gg.mmorealms.module.auction_house.backend.common.gui.impl;

import gg.mmorealms.module.auction_house.backend.common.dto.AuctionHouseListingEntry;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.backend.common.gui.ListingConfirmationGUI;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;

public class PokemonListingConfirmationGUI extends ListingConfirmationGUI<AuctionHouseListingEntry.Pokemon> {

	private final int index;

	public PokemonListingConfirmationGUI(User user, int index, IPokemon soldPokemon, int price) {
		super(user, AuctionHouseEntry.Type.POKEMON, new AuctionHouseListingEntry.Pokemon(soldPokemon), price);
		this.index = index;
	}

	@Override
	protected void cancelListing() {
		PokemonBackendModule.instance()
			.getPlatformImplementation()
			.getParty(user.getPlayer())
			.setPokemon(this.index, this.soldEntry.pokemon());
	}
}
