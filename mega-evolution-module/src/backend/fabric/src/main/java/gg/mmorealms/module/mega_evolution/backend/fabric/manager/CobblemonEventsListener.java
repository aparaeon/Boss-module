package gg.mmorealms.module.mega_evolution.backend.fabric.manager;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.pokemon.HeldItemEvent;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import com.cobblemon.mod.common.net.messages.client.pokemon.update.AbilityUpdatePacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import kotlin.Unit;


public class CobblemonEventsListener {

    private CobblemonEventsListener() {
    }

    public static void register() {
        CobblemonEvents.HELD_ITEM_PRE.subscribe(Priority.HIGHEST, CobblemonEventsListener::onHeldItemChange);
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.HIGHEST, CobblemonEventsListener::onBattleFainted);
        // A bit strange, but 'started_post' is called not after starting logic is finished, but when battle ends
        CobblemonEvents.BATTLE_STARTED_POST.subscribe(Priority.HIGHEST, CobblemonEventsListener::onBattleEnded);
        CobblemonEvents.MEGA_EVOLUTION.subscribe(Priority.NORMAL, CobblemonEventsListener::onMegaEvolution);
    }

    private static void onHeldItemChange(HeldItemEvent event) {
        Pokemon pokemon = event.getPokemon();

        boolean isRequiredGem = MegaEvolutionUtils.isHoldingRequiredGem(pokemon);
        if (isRequiredGem) {
            MegaEvolutionUtils.megaDevolve(pokemon);
        }
    }

    private static void onBattleFainted(BattleFaintedEvent event) {
        Pokemon pokemon = event.getKilled().getOriginalPokemon();

        // Mega devolve only if mega evolved inside the battle, just for quality of life
        if (MegaEvolutionUtils.isMegaEvolvedInBattle(pokemon)) {
            MegaEvolutionUtils.megaDevolve(pokemon);
        }
    }

    private static void onBattleEnded(BattleStartedEvent.Post event) {
        event.getBattle().getOnEndHandlers().add(battle -> {
            battle.getPlayers().forEach(player -> {
                PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);

                // Mega devolve only if mega evolved inside the battle, just for quality of life
                party.forEach(pokemon -> {
                    if (MegaEvolutionUtils.isMegaEvolvedInBattle(pokemon)) {
                        MegaEvolutionUtils.megaDevolve(pokemon);
                    }
                });
            });

            return Unit.INSTANCE;
        });
    }


    private static void onMegaEvolution(MegaEvolutionEvent event) {
        PokemonBattle battle = event.getBattle();
        BattlePokemon battlePokemon = event.getPokemon();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();
        BattleActor battleActor = battlePokemon.getActor();

        boolean isByPlayer = battleActor.getType().equals(ActorType.PLAYER);

        if (isByPlayer) {
            battle.sendUpdate(new AbilityUpdatePacket(() -> pokemon, pokemon.getAbility().getTemplate()));
            battlePokemon.sendUpdate();
        }

        for (ActiveBattlePokemon activeBattlePokemon : battle.getActivePokemon()) {
            if (!isByPlayer) {
                continue;
            }

            BattlePokemon currentBattlePokemon = activeBattlePokemon.getBattlePokemon();
            if (currentBattlePokemon == null) {
                continue;
            }

            // Update portrait
            if (currentBattlePokemon == battlePokemon) {
                battle.sendSidedUpdate(activeBattlePokemon.getActor(),
                        new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), battlePokemon, true),
                        new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), battlePokemon, false),
                        false);
            }
        }
    }

}
