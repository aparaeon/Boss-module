package gg.mmorealms.module.catch_combo.backend.fabric.manager;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.catch_combo.backend.fabric.CatchComboFabricModule;
import gg.mmorealms.module.catch_combo.backend.fabric.config.CatchComboConfig;
import gg.mmorealms.module.catch_combo.backend.fabric.database.ICatchCombo;
import gg.mmorealms.module.catch_combo.backend.fabric.utils.PokemonUtils;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class CobblemonEventsListener {

    private CobblemonEventsListener() { }

    public static void register() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(CobblemonEventsListener::onPokemonCaptured);
        CobblemonEvents.BATTLE_FLED.subscribe(CobblemonEventsListener::onBattleFled);
        CobblemonEvents.BATTLE_VICTORY.subscribe(CobblemonEventsListener::onBattleVictory);
        CobblemonEvents.SHINY_CHANCE_CALCULATION.subscribe(Priority.LOWEST, CobblemonEventsListener::onShinyChanceCalculation);
    }

    private static void onPokemonCaptured(PokemonCapturedEvent event) {
        ServerPlayer player = event.getPlayer();
        ICatchCombo catchCombo = ICatchCombo.get(player);

        Pokemon pokemon = event.getPokemon();
        User user = User.get(player);
        String oldSpecies = catchCombo.getSpeciesName();

        boolean hadCombo = catchCombo.hasCombo();
        boolean isSameSpecies = catchCombo.tryIncrementCombo(pokemon);
        boolean hasCombo = catchCombo.hasCombo();

        if (hadCombo || hasCombo) {
            MessageBuilder messageBuilder = isSameSpecies
                    ? lang().comboCorrectPokemon
                    : lang().comboWrongPokemon;

            MessageBuilder message = catchCombo.parseComboMessage(messageBuilder)
                    .parse("oldSpecies", oldSpecies);

            user.sendMessage(message);
        }
    }

    private static void onBattleVictory(BattleVictoryEvent event) {
        if (!event.getBattle().isPvW()) {
            return;
        }

        if (event.getWasWildCapture()) {
            return;
        }

        BattleActor winner = event.getWinners().getFirst();
        BattleActor loser = event.getLosers().getFirst();

        BattleActor battlePlayer;
        BattleActor battlePokemon;
        MessageBuilder messageBuilder;

        boolean isPlayerLost = loser.getType().equals(ActorType.PLAYER);
        if (isPlayerLost) {
            battlePlayer = loser;
            battlePokemon = winner;
            messageBuilder = lang().comboBattleLost;
        } else {
            battlePlayer = winner;
            battlePokemon = loser;
            messageBuilder = lang().comboPokemonFainted;
        }

        UUID playerUUID = battlePlayer.getUuid();
        Pokemon pokemon = battlePokemon.getPokemonList().getFirst().getOriginalPokemon();
        ICatchCombo catchCombo = ICatchCombo.get(playerUUID);

        // If Lost any battle, or won battle with (killed) target pokemon
        if (catchCombo.isSameSpecies(pokemon) || isPlayerLost) {
            catchCombo.resetAndNotifyIfHasCombo(playerUUID, messageBuilder);
        }
    }

    private static void onBattleFled(BattleFledEvent event) {
        ServerPlayer player = event.getPlayer().getEntity();
        if (player == null) {
            return;
        }

        ICatchCombo catchCombo = ICatchCombo.get(player);
        Pokemon wildPokemon = PokemonUtils.getWildPokemon(event.getBattle());

        if (wildPokemon != null && catchCombo.isSameSpecies(wildPokemon)) {
            catchCombo.resetAndNotifyIfHasCombo(player, lang().comboBattleFled);
        }
    }

    private static void onShinyChanceCalculation(ShinyChanceCalculationEvent event) {
        event.addModificationFunction((chance, player, pokemon) -> {
            if (player == null) {
                return chance;
            }

            ICatchCombo catchCombo = ICatchCombo.get(player);
            if (!catchCombo.isSameSpecies(pokemon)) {
                return chance;
            }

            float chancePercentage = 100f / chance;
            return 100f / (chancePercentage + catchCombo.getShinyRateBonus());
        });
    }

    private static CatchComboConfig.Lang lang() {
        return CatchComboFabricModule.instance().getConfig().lang;
    }

}
