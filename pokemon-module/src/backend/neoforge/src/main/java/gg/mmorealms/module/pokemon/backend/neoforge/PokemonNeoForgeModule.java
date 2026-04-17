package gg.mmorealms.module.pokemon.backend.neoforge;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.init.PixelmonInitEvent;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.pokemon.PokemonModuleBuildConstants;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.neoforge.manager.PixelmonPlatformImplementation;
import gg.mmorealms.module.pokemon.backend.neoforge.manager.PixelmonStorageAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod(PokemonModuleBuildConstants.ID)
public class PokemonNeoForgeModule extends PokemonBackendModule {

	@Getter
	@Accessors(fluent = true)
	private static PokemonNeoForgeModule instance;

	public PokemonNeoForgeModule() {
		PokemonNeoForgeModule.instance = this;
		this.setup();

		Logger.debug("Initializing NeoForge module for Pixelmon backend...");
		Pixelmon.EVENT_BUS.addListener(PokemonNeoForgeModule::onPixelmonInit);
//		Pixelmon.EVENT_BUS.register(new PixelmonEventManager()); // TODO Enable
	}

	@SubscribeEvent
	public static void onPixelmonInit(PixelmonInitEvent event) {
		event.setSaveAdapter(new PixelmonStorageAdapter(event.getSaveAdapter()));
	}

	@Override
	protected PokemonPlatformImplementation createPlatformImplementation() {
		return new PixelmonPlatformImplementation();
	}

	@Override
	public PixelmonPlatformImplementation getPlatformImplementation() {
		return (PixelmonPlatformImplementation) super.getPlatformImplementation();
	}
}
