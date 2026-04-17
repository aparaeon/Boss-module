package gg.mmorealms.module.pokemon.backend.fabric.dto.event;

import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.pokedex.scanner.PokedexEntityData;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PrePokedexDataChanged extends LocalRequest<Boolean> {

	private final PokedexEntityData dataSource;
	private final PokedexEntryProgress knowledge;
	private final UUID playerUUID;
	private final FormDexRecord record;

	public PrePokedexDataChanged(PokedexEntityData dataSource, PokedexEntryProgress knowledge, UUID playerUUID, FormDexRecord record) {
		super(true);
		this.dataSource = dataSource;
		this.knowledge = knowledge;
		this.playerUUID = playerUUID;
		this.record = record;
	}
}
