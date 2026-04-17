package gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation;

import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonPC;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

@Getter
public class PixelmonPC implements IPokemonPC {

	private final static RegistryAccess REGISTRY_ACCESS = PokemonBackendModule.instance().getRegistryAccess();

	private final PCStorage nativePC;

	public PixelmonPC(PCStorage nativePC) {
		this.nativePC = nativePC;
	}

	@Override
	public PCStorage getNative() {
		return nativePC;
	}

	@Override
	public JsonObject serialize() {
		CompoundTag nbt = new CompoundTag();
		nativePC.writeToNBT(nbt, REGISTRY_ACCESS);

		return CodecUtils.serialize(CompoundTag.CODEC, nbt);
	}
}
