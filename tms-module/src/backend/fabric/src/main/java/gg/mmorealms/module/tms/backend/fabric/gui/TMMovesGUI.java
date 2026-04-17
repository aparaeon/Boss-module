package gg.mmorealms.module.tms.backend.fabric.gui;

import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.PagedGUI;
import gg.mmorealms.module.tms.backend.fabric.TMsFabricModule;
import gg.mmorealms.module.tms.backend.fabric.config.TMsConfig;
import gg.mmorealms.module.tms.backend.fabric.gui.items.TMGUIItem;
import gg.mmorealms.module.tms.backend.fabric.utils.TMsUtils;

import java.util.List;

public class TMMovesGUI extends PagedGUI {
	private final TMsConfig.TMMovesGUIConfig guiConfig;
	private final Pokemon pokemon;
	private final List<String> tms;

	public TMMovesGUI(User user, Pokemon pokemon) {
		super(
				user,
				new GUI.Settings()
						.chestSize(TMsFabricModule.instance().getConfig().tmMovesGUIConfig.guiRows)
		);

		TMsConfig config = TMsFabricModule.instance().getConfig();
		this.guiConfig = config.tmMovesGUIConfig;
		this.pokemon = pokemon;
		this.tms = config.sortedTmMoves;
	}

	@Override
	public void setup() {
		guiConfig.background.forEach(this::setButton);
		setButton(guiConfig.previousPageItem)
				.onClick(this::previousPage);
		setButton(guiConfig.nextPageItem)
				.onClick(this::nextPage);

		List<Integer> slots = guiConfig.slots;
		List<String> tmsForPokemon = TMsUtils.getTMsForPokemon(tms, pokemon);

		populateSlots(tmsForPokemon, slots);
	}

	@Override
	public String getTitleString() {
		return guiConfig.title
				.parse("pokemonName", pokemon.getSpecies().getName())
				.parse("page", getPage() + 1)
				.toString();
	}

	private void populateSlots(List<String> tms, List<Integer> slots) {
		int startIndex = slots.size() * getPage();

		for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
			int itemIdx = startIndex + slotIdx;
			if (itemIdx >= tms.size()) {
				break;
			}

			String tmName = tms.get(itemIdx);
			if (tmName == null) {
				continue;
			}

			Integer slot = slots.get(slotIdx);
			if (slot == null) {
				continue;
			}

			GUIButton button = new TMGUIItem(user, pokemon, tmName)
					.toGUIButton(slot, this::refresh);

			setButton(button);
		}
	}

	@Override
	protected int getPagesCount() {
		List<Integer> slots = guiConfig.slots;
		List<String> tmsForPokemon = TMsUtils.getTMsForPokemon(tms, pokemon);

		int itemsPerPage = slots.size();
		int totalItems = tmsForPokemon.size();

		return (totalItems > 0)
				? ((totalItems - 1) / itemsPerPage) + 1
				: 1;
	}
}
