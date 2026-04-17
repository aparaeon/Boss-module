package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.DataKeys;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokedex;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonPC;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.fabric.PokemonFabricModule;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPC;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonParty;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPokedex;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPokemon;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CobblemonPlatformImplementation extends PokemonPlatformImplementation {

	private static final RegistryAccess REGISTRY_ACCESS = PokemonFabricModule.instance().getRegistryAccess();

	@Override
	public IPokemon create(String speciesName, boolean shiny) {
		if (speciesName == null) {
			return null;
		}

		Species species = PokemonSpecies.getByName(speciesName);

		if (species == null) {
			return null;
		}

		Pokemon pokemon = species.create(1);
		pokemon.setShiny(shiny);

		return new CobblemonPokemon(pokemon);
	}

	@Override
	public IPokemon fromProperties(String propertiesString) {
		MinecraftServer server = PokemonFabricModule.instance().getServer();
		CommandContext<CommandSourceStack> context = server.getCommands().getDispatcher()
				.parse("pokegive " + propertiesString, server.createCommandSourceStack())
				.getContext().build(propertiesString);
		PokemonProperties properties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, "properties");
		return new CobblemonPokemon(properties.create());
	}

	@Override
	public IPokemon getPokemonFromEntity(Entity entity) {
		if (!(entity instanceof PokemonEntity pokemonEntity)) {
			return null;
		}

		Pokemon pokemon = pokemonEntity.getPokemon();
		return new CobblemonPokemon(pokemon);
	}

	@Override
	public IPokemon getPokemonFromItem(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof PokemonItem pokemonItem)) {
			return null;
		}

		return new CobblemonPokemon(pokemonItem.asPokemon(itemStack));
	}

	@Override
	public IPokedex createEmptyPokedex(UUID uuid) {
		return new CobblemonPokedex(new PokedexManager(uuid, new HashMap<>()));
	}

	@Override
	public IPokemonParty getParty(UUID uuid) {
		PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(uuid, REGISTRY_ACCESS);
		return new CobblemonParty(party);
	}

	@Override
	public IPokemonPC getPC(ServerPlayer player) {
		PCStore pc = Cobblemon.INSTANCE.getStorage().getPC(player);
		return new CobblemonPC(pc);
	}

	@Override
	public IPokedex getPokedex(ServerPlayer player) {
		PokedexManager pokedexManager = Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(player.getUUID());
		return new CobblemonPokedex(pokedexManager);
	}

	@Override
	public IPokemon deserializePokemon(JsonObject json) {
		Pokemon pokemon = CodecUtils.deserialize(Pokemon.getCODEC(), json, CodecUtils.CodecErrorProcessor.ofNull());
		return new CobblemonPokemon(pokemon);
	}

	@Override
	public CobblemonParty deserializeParty(UUID owner, JsonObject json) {
		if (json == null) {
			PlayerPartyStore party = new PlayerPartyStore(owner, owner);
			return new CobblemonParty(party);
		}

		PlayerPartyStore party = new PlayerPartyStore(owner, owner);
		party.loadFromJSON(json, REGISTRY_ACCESS);
		return new CobblemonParty(party);
	}

	@Override
	public CobblemonPC deserializePC(UUID owner, JsonObject json) {
		PCStore pcStore = new PCStore(owner);
		Function1<Pokemon, Unit> overflowHandler = pokemon -> {
			pcStore.relocateEvictedBoxPokemon(pokemon);
			return Unit.INSTANCE;
		};

		if (json == null) {
			pcStore.resize(Cobblemon.config.getDefaultBoxCount(), false, overflowHandler);
			return new CobblemonPC(pcStore);
		}

		json = fixPC(json);
		pcStore.loadFromJSON(json, REGISTRY_ACCESS);
		pcStore.resize(Cobblemon.config.getDefaultBoxCount(), false, overflowHandler);
		return new CobblemonPC(pcStore);
	}

	private JsonObject fixPC(JsonObject json) {
		if (json == null) {
			return null;
		}

		if (!json.has(DataKeys.STORE_UNLOCKED_WALLPAPERS)) {
			json.add(DataKeys.STORE_UNLOCKED_WALLPAPERS, new JsonArray());
		}

		if (!json.has(DataKeys.STORE_UNSEEN_WALLPAPERS)) {
			json.add(DataKeys.STORE_UNSEEN_WALLPAPERS, new JsonArray());
		}

		return json;
	}

	@Override
	public CobblemonPokedex deserializePokedex(UUID owner, JsonObject json) {
		PokedexManager pokedex = CodecUtils.deserialize(PokedexManager.Companion.getCODEC(), json, CodecUtils.CodecErrorProcessor.of(
				() -> new PokedexManager(owner, new HashMap<>())
		));

		return new CobblemonPokedex(pokedex);
	}

	@Override
	public Class<?> getNativePokemonClass() {
		return Pokemon.class;
	}

	@Override
	public Class<?> getNativePokedexClass() {
		return PokedexManager.class;
	}

	@Override
	public Class<?> getNativePartyClass() {
		return PartyStore.class;
	}

	@Override
	public Class<?> getNativePCClass() {
		return PCStore.class;
	}

	@Override
	public Class<? extends Entity> getNativePokemonEntityClass() {
		return PokemonEntity.class;
	}
}
