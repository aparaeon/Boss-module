package gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation;

import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import gg.mmorealms.module.pokemon.backend.neoforge.PokemonNeoForgeModule;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

public class PixelmonParty implements IPokemonParty {

	private final static RegistryAccess REGISTRY_ACCESS = PokemonBackendModule.instance().getRegistryAccess();

	private final PlayerPartyStorage nativeParty;

	public PixelmonParty(PlayerPartyStorage nativeParty) {
		this.nativeParty = nativeParty;
	}

	@Override
	public PlayerPartyStorage getNative() {
		return nativeParty;
	}

	@Override
	public JsonObject serialize() {
		CompoundTag nbt = new CompoundTag();
		nativeParty.writeToNBT(nbt, REGISTRY_ACCESS);

		return CodecUtils.serialize(CompoundTag.CODEC, nbt);
	}

	@Override
	public IPokemon getPokemon(int index) {
		Pokemon nativePokemon = nativeParty.get(index);

		if (nativePokemon == null) {
			return null;
		}

		return new PixelmonPokemon(nativeParty.get(index));
	}

	@Override
	public Integer getSize() {
		return nativeParty.maxSize();
	}

	@Override
	public void setPokemon(int index, IPokemon pokemon) {
		if (pokemon == null) {
			nativeParty.set(index, null);
			return;
		}

		nativeParty.set(index, (Pokemon) pokemon.getNative());
	}

	@Override
	// TODO: Add offline and different server support
	public void add(IPokemon pokemon) {
		nativeParty.add((Pokemon) pokemon.getNative());
	}

	public void add(JsonObject serializedPokemon) {
		add(PokemonNeoForgeModule.instance().getPlatformImplementation().deserializePokemon(serializedPokemon));
	}

}
