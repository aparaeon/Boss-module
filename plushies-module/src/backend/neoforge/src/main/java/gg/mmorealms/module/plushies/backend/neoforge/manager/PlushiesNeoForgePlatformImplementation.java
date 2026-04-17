package gg.mmorealms.module.plushies.backend.neoforge.manager;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule;
import gg.mmorealms.module.plushies.backend.common.manager.IPlushiesPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation.PixelmonPokemon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import static gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule.PLUSHIE_TAG;

public class PlushiesNeoForgePlatformImplementation implements IPlushiesPlatformImplementation {

	public Entity createPlushieEntity(ServerLevel world, IPokemon pokemon) {
		Pokemon nativePokemon = (Pokemon) pokemon.getNative();
		nativePokemon.addFlag("unbattleable");
		nativePokemon.addFlag("uncatchable");
		PixelmonEntity entity = new PixelmonPokemon(nativePokemon).createEntity(world);

		// Only apply the scale if the scale modifier of the Pokémon is 1 (default).
		if (pokemon.getScale() == 1) {
			float scale = PlushiesBackendModule.instance().getConfig().plushiesScale.getOrDefault(pokemon.getSpeciesName(), 1f);
			pokemon.setScale(scale);
		}

		entity.setNoAi(true);
		entity.setPersistenceRequired();
		entity.setTamed(true);
		entity.setInvulnerable(true);
		entity.setNoGravity(true);
		entity.addTag(PLUSHIE_TAG);

		return entity;
	}

}
