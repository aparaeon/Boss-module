package gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.google.gson.JsonObject;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonPC;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;

@Getter
public class CobblemonPC implements IPokemonPC {

	private final static RegistryAccess REGISTRY_ACCESS = PokemonBackendModule.instance().getRegistryAccess();

	private final PCStore nativePC;

	public CobblemonPC(PCStore nativePC) {
		this.nativePC = nativePC;
	}

	@Override
	public PCStore getNative() {
		return nativePC;
	}

	@Override
	public JsonObject serialize() {
		JsonObject object = new JsonObject();

		PCStore pc = Cobblemon.INSTANCE.getStorage().getPC(nativePC.getUuid(), REGISTRY_ACCESS);
		pc.saveToJSON(object, REGISTRY_ACCESS);

		return object;
	}
}
