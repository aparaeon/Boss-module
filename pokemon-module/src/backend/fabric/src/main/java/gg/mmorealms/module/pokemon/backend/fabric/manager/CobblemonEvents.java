package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.pokemon.backend.common.dto.database.StarterData;
import gg.mmorealms.module.pokemon.backend.common.dto.event.PokemonCapturedEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.event.PokemonSpawnEvent;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleStartRequest;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleWonEvent;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.PrePokedexDataChanged;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPokemon;
import gg.mmorealms.module.pokemon.common.dto.PlayerChoseStarterS2PEvent;
import gg.mmorealms.module.pokemon.common.dto.PlayerSendPokemonS2PEvent;
import gg.mmorealms.module.pokemon.common.dto.Response;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

// TODO move the event logic out of this class
public class CobblemonEvents {

	public CobblemonEvents() {
		registerEvents();
	}

	public void registerEvents() {
		com.cobblemon.mod.common.api.events.CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.HIGHEST, (event) -> {
			try {
				Logger.info(new MessageBuilder("{player} chose starter {pokemon}")
						.parse("player", event.getPlayer().getName().getString())
						.parse("pokemon", CobblemonPrintUtils.getBriefDescription(event.getPokemon()))
				);
				new StarterData(event.getPlayer().getUUID()).save();
			} catch (DatabaseSaveException exception) {
				Logger.error(exception);
				return Unit.INSTANCE;
			}
			new PlayerChoseStarterS2PEvent(event.getPlayer().getUUID()).send();
			return Unit.INSTANCE;
		});

		com.cobblemon.mod.common.api.events.CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, (event) -> {
			new PlayerSendPokemonS2PEvent(event.getPokemon().getOwnerUUID()).send();
			return Unit.INSTANCE;
		});

		com.cobblemon.mod.common.api.events.CobblemonEvents.POKEDEX_DATA_CHANGED_PRE.subscribe(Priority.NORMAL, (__event) -> {
			PrePokedexDataChanged event = new PrePokedexDataChanged(
					__event.getDataSource(),
					__event.getKnowledge(),
					__event.getPlayerUUID(),
					__event.getRecord()
			);

			event.fireSync();

			if (!event.getResult()) {
				__event.cancel();
			}

			return null;
		});

		com.cobblemon.mod.common.api.events.CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(Priority.NORMAL, (event) -> null);

		com.cobblemon.mod.common.api.events.CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL,
				(event) -> {
					Logger.info(new MessageBuilder("{player} captured with {pokeball} {pokemon}")
							.parse("player", event.getPlayer().getName().getString())
							.parse("pokeball", event.getPokeBallEntity().getPokeBall().getName().getPath())
							.parse("pokemon", CobblemonPrintUtils.getBriefDescription(event.getPokemon()))
					);

                    ServerPlayer player = event.getPlayer();
                    PokemonEntity pokemonEntity = event.getPokeBallEntity().getCapturingPokemon();
                    new PokemonCapturedEvent(player, new CobblemonPokemon(event.getPokemon()), pokemonEntity).fireAsync();

					return Unit.INSTANCE;
				}
		);

		com.cobblemon.mod.common.api.events.CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL,
				(event) -> {
					Logger.info(new MessageBuilder("{player} released his {pokemon}")
							.parse("player", event.getPlayer().getName().getString())
							.parse("pokemon", CobblemonPrintUtils.getBriefDescription(event.getPokemon()))
					);

					return Unit.INSTANCE;
				}
		);

		com.cobblemon.mod.common.api.events.CobblemonEvents.TRADE_EVENT_POST.subscribe(Priority.NORMAL,
				(event) -> {
					Logger.info(new MessageBuilder("{player1} traded his {pokemon1} with {player2} for {pokemon2}")
							.parse("player1", event.getTradeParticipant1().getName().getString())
							.parse("pokemon1", CobblemonPrintUtils.getBriefDescription(event.getTradeParticipant2Pokemon()))
							.parse("player2", event.getTradeParticipant2().getName().getString())
							.parse("pokemon2", CobblemonPrintUtils.getBriefDescription(event.getTradeParticipant1Pokemon()))
					);

					return Unit.INSTANCE;
				}
		);

		com.cobblemon.mod.common.api.events.CobblemonEvents.EVOLUTION_ACCEPTED.subscribe(Priority.NORMAL,
				(event) -> {
					Logger.info(new MessageBuilder("{player} evolved {pokemon1} into {pokemon2}")
							.parse("player", event.getPokemon().getOwnerPlayer().getName().getString())
							.parse("pokemon1", event.getPokemon().getSpecies().getName())
							.parse("pokemon2", event.getEvolution().getResult().getSpecies())
					);

					return Unit.INSTANCE;
				}
		);

		com.cobblemon.mod.common.api.events.CobblemonEvents.ENTITY_SPAWN.subscribe(Priority.HIGHEST,
				(event) -> {
					PokemonSpawnEvent pokemonSpawnEvent = new PokemonSpawnEvent(event.getEntity());

					if (!pokemonSpawnEvent.getResult()) {
						event.cancel();
					}

					return Unit.INSTANCE;
				}
		);

		com.cobblemon.mod.common.api.events.CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.HIGHEST, (event) -> {
			Response response = new BattleStartRequest(event).fireSync();

			if (!response.isAccepted()) {
				IUser user = IUser.getByPlayer(event.getBattle().getPlayers().getFirst());
				user.sendMessage(response.getMessage());
				event.cancel();
			}

			return Unit.INSTANCE;
		});

		com.cobblemon.mod.common.api.events.CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, (event) -> {
			new BattleWonEvent(event).fireAsync();

			return Unit.INSTANCE;
		});
	}
}