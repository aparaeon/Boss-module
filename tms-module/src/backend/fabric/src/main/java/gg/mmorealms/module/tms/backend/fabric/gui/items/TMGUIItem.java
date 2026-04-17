package gg.mmorealms.module.tms.backend.fabric.gui.items;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.tms.backend.fabric.TMsFabricModule;
import gg.mmorealms.module.tms.backend.fabric.config.TMElementalType;
import gg.mmorealms.module.tms.backend.fabric.config.TMsConfig;
import gg.mmorealms.module.tms.backend.fabric.utils.PokemonUtils;
import gg.mmorealms.module.tms.backend.fabric.utils.TMsUtils;

import java.util.Map;

public class TMGUIItem {
	private final IUser user;
	private final Pokemon pokemon;
	private final String tmName;

	private final MoveTemplate moveTemplate;
	private final String moveName;

	private final Price tmPrice;
	private final double currencyAmount;

	private final GUIButton baseItem;

	public TMGUIItem(IUser user, Pokemon pokemon, String tmName) {
		TMsConfig config = TMsFabricModule.instance().getConfig();

		this.user = user;
		this.pokemon = pokemon;
		this.tmName = tmName;

		this.tmPrice = config.tmMoves.getOrDefault(tmName, new Price(CurrencyType.POKECOINS, 0));
		this.currencyAmount = IBalances.getByUser(user)
				.get(tmPrice.currency());

		this.moveTemplate = TMsUtils.getTMMoveTemplate(tmName);
		TMElementalType elementalType = TMElementalType.NORMAL;

		if (moveTemplate == null) {
			this.moveName = "";
		} else {
			this.moveName = TMsUtils.getTMMoveTemplateName(this.moveTemplate);
			elementalType = TMElementalType.fromElementalType(moveTemplate.getElementalType());
		}

		this.baseItem = config.tmMovesGUIConfig.tmElementToItem
				.get(elementalType)
				.clone();
	}

	public GUIButton toGUIButton(int slot, Runnable onClick) {
		String tmPriceTypeName = tmPrice.currency().getName();

		return baseItem.clone()
				.position(slot)
				.placeholders(Map.of(
						"moveName", moveName,
						"tmPriceAmount", tmPrice.amount(),
						"tmPriceType", tmPriceTypeName,
						"currencyAmount", currencyAmount,
						"currencyType", tmPriceTypeName
				))
				.onClick(clickType -> {
					if (!clickType.isLeft) {
						return;
					}

					if (PokemonUtils.canPokemonLearnMove(pokemon, moveTemplate)
							&& TMsUtils.buyTm(user, pokemon, tmName)) {

						PokemonUtils.teachPokemonMove(pokemon, moveTemplate);
						onClick.run();
					}
				});
	}
}
