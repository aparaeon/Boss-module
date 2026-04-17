package gg.mmorealms.module.tms.backend.fabric.gui;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.tms.backend.fabric.TMsFabricModule;
import gg.mmorealms.module.tms.backend.fabric.config.TMsConfig;
import gg.mmorealms.module.tms.backend.fabric.gui.items.PokemonGUIItem;

import java.util.ArrayList;
import java.util.List;

public class PokemonPartyGUI extends GUI {
	private final User user;
	private final TMsConfig.PokemonPartyGUIConfig guiConfig;

	public PokemonPartyGUI(User user) {
		super(
				user,
				new GUI.Settings()
						.chestSize(TMsFabricModule.instance().getConfig().pokemonPartyGUIConfig.guiRows)
		);
		this.user = user;
		this.guiConfig = TMsFabricModule.instance().getConfig().pokemonPartyGUIConfig;
	}

	@Override
	public String getTitleString() {
		return guiConfig.title;
	}

	@Override
	public void setup() {
		guiConfig.background.forEach(this::setButton);

		populateSlots();
	}

	protected void populateSlots() {
		List<Pokemon> pokemons = getPlayerParty();
		List<Integer> slots = guiConfig.slots;

		int displayed = 0;
		for (int i = 0; i < pokemons.size() && displayed < slots.size(); i++) {
			Pokemon pokemon = pokemons.get(i);
			if (pokemon == null) {
				continue;
			}

			int slot = slots.get(displayed++);
			GUIButton button = new PokemonGUIItem(pokemon)
					.toGUIButton(slot, new TMMovesGUI(user, pokemon)::open);

			setButton(button);
		}
	}

	private List<Pokemon> getPlayerParty() {
		PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage()
				.getParty(user.getPlayer().getUUID(), TMsFabricModule.instance().getRegistryAccess());

		List<Pokemon> pokemonList = new ArrayList<>();
		for (int i = 0; i < partyStore.size(); i++) {
			pokemonList.add(partyStore.get(i));
		}

		return pokemonList;
	}
}
