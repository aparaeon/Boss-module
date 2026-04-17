package gg.mmorealms.module.pokemon.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.world.entity.Entity;

@Getter
public class PokemonSpawnEvent extends LocalRequest<Boolean> {

	private final Entity entity;

	public PokemonSpawnEvent(Entity entity) {
		super(true);
		this.entity = entity;
	}

}
