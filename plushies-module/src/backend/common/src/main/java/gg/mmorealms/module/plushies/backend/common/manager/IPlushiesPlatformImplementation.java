package gg.mmorealms.module.plushies.backend.common.manager;

import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public interface IPlushiesPlatformImplementation {

	Entity createPlushieEntity(ServerLevel world, IPokemon pokemon);

}
