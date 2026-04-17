package gg.mmorealms.module.pokemon.backend.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.StreamableResource;
import gg.mmorealms.loader.common.dto.event.impl.StreamStartRequest;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.CommonPermissions;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.PokemonConfig;
import gg.mmorealms.module.pokemon.backend.common.dto.event.SetPokemonEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;

import java.util.List;
import java.util.UUID;

public class PokeSeeGUI extends GUI {

	public static final String POKE_SEE_STREAM_TYPE = "poke_see";

	private final UUID targetUUID;
	private final PokemonConfig.PokeSeeGUI config;

	private int offlineStreams = 0;
	private int ticks;

	public PokeSeeGUI(User user, UUID targetUUID) {
		super(user, new Settings().chestSize(6));
		this.targetUUID = targetUUID;
		this.config = PokemonBackendModule.instance().getConfig().pokeSeeGUI;
	}

	@Override
	public String getTitleString() {
		IUser target = IUser.getByUUID(targetUUID);
		return new MessageBuilder("{target}'s Party") // TODO Config
				.parse("target", target.getUsername())
				.parse();
	}

	@Override
	public void setup() {
		setButton(config.backgroundItem);

		String streamData = StreamableResource.streamData(POKE_SEE_STREAM_TYPE + "#" + targetUUID);

		if (streamData == null) {
			setButton(config.temporaryOfflineStream
					.position(config.statusIndex)
			);
			offlineStreams++;

			IUser target = IUser.getByUUID(targetUUID);
			new StreamStartRequest(target.getServerLocation().getServer(), POKE_SEE_STREAM_TYPE, targetUUID.toString()).sendAndGet();
			return;
		}

		setButton(
				config.onlineStream
						.position(config.statusIndex)
		);

		List<IPokemon> party = IPokemonParty.deserializeParty(streamData);
		for (int index = 0; index < config.pokemonSlots.size(); index++) {
			int pokemonSlot = config.pokemonSlots.get(index);
			IPokemon pokemon = party.get(index);

			if (pokemon.getNative() == null) {
				continue;
			}

			ItemBuilder itemBuilder = pokemon.toItemBuilder(true);

			if (user.hasPermission(CommonPermissions.ADMIN)) {
				itemBuilder
						.addLore("")
						.addLore("<red>Click to remove");
			}

			int finalIndex = index;
			setButton(
					new GUIButton()
							.display(itemBuilder)
							.position(pokemonSlot)
							.onClick((click) -> removePokemon(click, finalIndex))
			);
		}
	}

	private void removePokemon(ClickType click, int slot) {
		IUser target = IUser.getByUUID(targetUUID);

		if (target == null || !target.isOnlineOnNetwork()) {
			user.sendMessage("The target user is not online.");
			return;
		}

		if (!user.hasPermission(CommonPermissions.ADMIN)) {
			user.sendMessage("You do not have permission to remove Pokémon from this party.");
			return;
		}

		new SetPokemonEvent(
				target.getServerLocation().getServer(),
				targetUUID,
				slot,
				null
		).send();
	}

	@Override
	public void onTick() {
		if (offlineStreams >= config.offlineStreamThreshold) {
			setButton(
					config.offlineStream
							.position(config.statusIndex)
			);
			return;
		}

		ticks++;

		if (ticks >= 4) {
			ticks = 0;
			refresh();
		}
	}
}
