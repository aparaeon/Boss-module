package gg.mmorealms.module.pokemon.backend.fabric;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes;
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules;
import com.raduvoinea.commandmanager.backend.fabric.FabricMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.database.PokedexData;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.fabric.manager.CobblemonEvents;
import gg.mmorealms.module.pokemon.backend.fabric.manager.CobblemonPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.fabric.manager.PokedexDataFactory;
import gg.mmorealms.module.pokemon.backend.fabric.manager.PokemonStoreFactory;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

@Getter
public class PokemonFabricModule extends PokemonBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	private static PokemonFabricModule instance;

	private @Inject MinecraftServer server;
	private @Inject RegistryAccess registryAccess;

	private @Inject FabricMiniMessageManager miniMessageManager;

	// Not really needed as a field, but to force garbage collector to not free it
	private CobblemonEvents cobblemonEvents;

	public PokemonFabricModule() {
		PokemonFabricModule.instance = this;
	}

	@Override
	public void onInit() {
		super.onInit();

		// We only need pokedex to be saved on fabric (ie cobblestone) as neo forge (ie pixelmon) does save it in party (why party? do not ask me)
		BackendPlayerDependentDatabaseLoader.registerPlayerMethod(
				"Pokedex Data",
				(player) -> {

				},
				(player) -> {
					try {
						new PokedexData(player).save();
					} catch (DatabaseSaveException e) {
						Logger.error(e);
					}
				},
				Time.minutes(5)
		);

		cobblemonEvents = new CobblemonEvents();
	}

	@Override
	public void onEnable() {
		// TODO: Most probably needed on pixelmon too
		Cobblemon.INSTANCE.getStorage().unregisterAll(PokemonFabricModule.instance().getRegistryAccess());
		Cobblemon.INSTANCE.getStorage().registerFactory(Priority.HIGHEST, new PokemonStoreFactory());

		Cobblemon.INSTANCE.getPlayerDataManager().setFactory(new PokedexDataFactory(), PlayerInstancedDataStoreTypes.INSTANCE.getPOKEDEX());

		this.server.getGameRules().getRule(CobblemonGameRules.BATTLE_INVULNERABILITY).set(false, this.server);

		if (this.getServerType().equals(ServerType.SPAWN) || this.getServerType().equals(ServerType.GYMS)) {
			this.server.getGameRules().getRule(CobblemonGameRules.DO_POKEMON_LOOT).set(false, this.server);
			this.server.getGameRules().getRule(CobblemonGameRules.SHINY_STARTERS).set(false, this.server);
			this.server.getGameRules().getRule(CobblemonGameRules.DO_POKEMON_SPAWNING).set(false, this.server);
			this.server.getGameRules().getRule(CobblemonGameRules.MOB_TARGET_IN_BATTLE).set(true, this.server);
		}
	}

	@Override
	protected PokemonPlatformImplementation createPlatformImplementation() {
		return new CobblemonPlatformImplementation();
	}

	@Override
	public CobblemonPlatformImplementation getPlatformImplementation() {
		return (CobblemonPlatformImplementation) super.getPlatformImplementation();
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}