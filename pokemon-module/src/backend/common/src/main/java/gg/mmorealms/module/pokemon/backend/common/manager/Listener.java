package gg.mmorealms.module.pokemon.backend.common.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.StreamableResource;
import gg.mmorealms.loader.common.dto.event.impl.StreamStartRequest;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.event.SetPokemonEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import gg.mmorealms.module.pokemon.backend.common.gui.PokeSeeGUI;
import net.minecraft.core.RegistryAccess;

import java.util.UUID;

public class Listener {

	private @Inject RegistryAccess registryAccess;
	private @Inject PokemonPlatformImplementation platformImplementation;

	@EventHandler
	public void onStreamStartRequest$PokeSee(StreamStartRequest event) {
		if (!event.getStreamType().equals(PokeSeeGUI.POKE_SEE_STREAM_TYPE)) {
			return;
		}

		UUID targetUUID = UUID.fromString(event.getStreamID());
		StreamableResource.create(
				event.getStreamChannel(),
				Time.milliseconds(400),
				Time.seconds(1),
				() -> {
					IUser targetPrimitive = IUser.getByUUID(targetUUID);

					if (!(targetPrimitive instanceof User user)) {
						return null;
					}

					IPokemonParty party = platformImplementation.getParty(user.getPlayer());
					return PokemonBackendModule.instance().toJson(IPokemonParty.serializeParty(party));
				}
		);

		event.setResult(true);
	}

	@EventHandler
	public void onSetPokemonEvent(SetPokemonEvent event) {
		IUser userPrimitive = IUser.getByUUID(event.getTargetUUID());

		if (!(userPrimitive instanceof User user)) {
			Logger.warn("Attempted to set pokemon for a non-user target: " + event.getTargetUUID());
			return;
		}

		IPokemonParty party = platformImplementation.getParty(user.getPlayer());
		IPokemon pokemon = event.getPokemon();

		party.setPokemon(event.getSlot(), pokemon);
	}

}
