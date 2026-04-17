package gg.mmorealms.module.tms.backend.fabric.gui.items;

import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPokemon;
import gg.mmorealms.module.tms.backend.fabric.TMsFabricModule;
import gg.mmorealms.module.tms.backend.fabric.config.TMsConfig;
import gg.mmorealms.module.tms.backend.fabric.utils.PokemonUtils;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector4f;

public class PokemonGUIItem {
	private final Pokemon pokemon;
	private final GUIButton baseItem;
	private final boolean isBattling;

	public PokemonGUIItem(Pokemon pokemon) {
		this.pokemon = pokemon;
		this.isBattling = PokemonUtils.isBattling(pokemon);
		this.baseItem = this.isBattling
				? getDisabledPokemonItem()
				: getEnabledPokemonItem();
	}

	private GUIButton getEnabledPokemonItem() {
		ItemBuilder itemBuilder = new CobblemonPokemon(pokemon).toItemBuilder(true);
		return new GUIButton()
				.display(itemBuilder);
	}

	private GUIButton getDisabledPokemonItem() {
		TMsConfig config = TMsFabricModule.instance().getConfig();
		Vector4f tint = new Vector4f(0.5f, 0.5f, 0.5f, 0.5f);
		ItemStack itemStack = PokemonItem.from(pokemon, 1, tint);

		String displayName = config.lang.cantLearnMoveInBattle
				.parse("pokemonName", pokemon.getSpecies().getName())
				.toString();

		return new GUIButton()
				.displayName(displayName)
				.display(itemStack);
	}

	public GUIButton toGUIButton(int slot, Runnable onClick) {
		return baseItem.clone()
				.position(slot)
				.onClick(clickType -> {
					if (!clickType.isLeft) {
						return;
					}

					if (isBattling) {
						return;
					}

					onClick.run();
				});
	}

}
