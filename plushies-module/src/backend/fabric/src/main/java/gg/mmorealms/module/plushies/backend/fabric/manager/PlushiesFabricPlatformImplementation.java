package gg.mmorealms.module.plushies.backend.fabric.manager;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule;
import gg.mmorealms.module.plushies.backend.common.manager.IPlushiesPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

import static gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule.PLUSHIE_TAG;

public class PlushiesFabricPlatformImplementation implements IPlushiesPlatformImplementation {

	public Entity createPlushieEntity(ServerLevel world, IPokemon pokemon) {
		PokemonEntity entity = new PokemonEntity(world, (Pokemon) pokemon.getNative(), CobblemonEntities.POKEMON);

		// Only apply the scale if the scale modifier of the Pokémon is 1 (default).
		if (pokemon.getScale() == 1) {
			float scale = PlushiesBackendModule.instance().getConfig().plushiesScale.getOrDefault(pokemon.getSpeciesName(), 1f);
			pokemon.setScale(scale);
		}

		entity.getPokemon().getCustomProperties().add(UncatchableProperty.INSTANCE.uncatchable());
		entity.setNoAi(true);
		entity.setPersistenceRequired();
		entity.setTame(true, false);
		entity.setInvulnerable(true);
		entity.hideNameRendering();
		entity.setBattleId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
		entity.getEntityData().set(PokemonEntity.Companion.getUNBATTLEABLE(), true);
		entity.setNoGravity(true);
		entity.addTag(PLUSHIE_TAG);

		return entity;
	}

}
