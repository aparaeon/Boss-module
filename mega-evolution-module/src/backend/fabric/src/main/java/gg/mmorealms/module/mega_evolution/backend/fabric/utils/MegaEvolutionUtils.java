package gg.mmorealms.module.mega_evolution.backend.fabric.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.storage.PokemonStore;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.StorePosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.animation.PlayPosableAnimationPacket;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.mega_evolution.backend.fabric.MegaEvolutionFabricModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.config.MegaEvolutionConfig;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IMegaPokemon;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IMegaPokemonEntity;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionItems;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

@SuppressWarnings("resource")
public class MegaEvolutionUtils {

    private MegaEvolutionUtils() { }

    public static void megaEvolve(Pokemon pokemon) {
        MegaEvolution evolution = getPossibleMegaEvolution(pokemon);
        if (evolution != null) {
            megaEvolve(pokemon, evolution);
        }
    }

    @Nullable
    public static Pokemon getPokemonInBattle(Pokemon pokemon) {
        ServerPlayer player = pokemon.getOwnerPlayer();
        if (player == null) {
            return null;
        }

        PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(player);
        if (battle == null) {
            return null;
        }

        return getPokemonInBattle(battle);
    }

    @Nullable
    public static Pokemon getPokemonInBattle(PokemonBattle battle) {
        return StreamSupport.stream(battle.getActivePokemon().spliterator(), false)
                .map(ActiveBattlePokemon::getBattlePokemon)
                .filter(Objects::nonNull)
                .map(BattlePokemon::getEffectedPokemon)
                .filter(Pokemon::isPlayerOwned)
                .findFirst()
                .orElse(null);
    }

    public static void megaEvolve(Pokemon pokemon, MegaEvolution evolution) {
        Pokemon playerPokemon;
        PokemonEntity pokemonEntity = pokemon.getEntity();

        if (pokemonEntity == null) {
            playerPokemon = getPokemonInBattle(pokemon);
            if (playerPokemon == null) {
                return;
            }

            pokemonEntity = playerPokemon.getEntity();
        } else {
            // Making it effectively final
            playerPokemon = pokemon;
        }

        if (pokemonEntity == null) {
            return;
        }

        IMegaPokemonEntity megaPokemonEntity = (IMegaPokemonEntity) pokemonEntity;
        megaPokemonEntity.mega_evolution$addMegaEvolutionLock();

        playMegaEvolutionAnimation(pokemonEntity);

        MegaEvolutionConfig config = MegaEvolutionFabricModule.instance().getConfig();
        pokemonEntity.after(config.megaEvolutionFormChangeDelay.toSeconds(), () -> {
            modifyAspects(pokemon, evolution.getMegaAspect(), true);
            modifyAspects(playerPokemon, evolution.getMegaAspect(), true);
            return Unit.INSTANCE;
        });

        pokemonEntity.after(config.megaEvolutionAnimationDuration.toSeconds(), () -> {
            megaPokemonEntity.mega_evolution$removeMegaEvolutionLock();
            return Unit.INSTANCE;
        });
    }

    private static void playMegaEvolutionAnimation(PokemonEntity pokemonEntity) {
        MegaEvolutionConfig config = MegaEvolutionFabricModule.instance().getConfig();

        PlayPosableAnimationPacket packet = new PlayPosableAnimationPacket(
                pokemonEntity.getId(),
                config.megaEvolutionAnimation,
                Collections.emptyList());

        packet.sendToPlayersAround(
                pokemonEntity.getX(),
                pokemonEntity.getY(),
                pokemonEntity.getZ(),
                config.megaEvolutionAnimationPacketDistance,
                pokemonEntity.level().dimension(),
                __ -> false);
    }

    public static void megaDevolve(Pokemon pokemon) {
        MegaEvolution evolution = getPossibleMegaEvolution(pokemon);
        if (evolution != null) {
            megaDevolve(pokemon, evolution);
        }
    }

    public static void megaDevolve(Pokemon pokemon, MegaEvolution evolution) {
        if (pokemon instanceof IMegaPokemon megaPokemon) {
            megaPokemon.setMegaEvolvedInBattle(false);
        }

        modifyAspects(pokemon, evolution.getMegaAspect(), false);
    }

    private static void modifyAspects(Pokemon pokemon, String aspect, boolean add) {
        Set<String> aspects = new HashSet<>(pokemon.getForcedAspects());

        if (add) {
            aspects.add(aspect);
        } else {
            aspects.remove(aspect);
        }

        pokemon.setForcedAspects(aspects);
    }

    public static boolean isHoldingRequiredGem(Pokemon pokemon) {
        Item requiredGem = getRequiredMegaGem(pokemon);
        if (requiredGem == null) {
            return false;
        }

        return pokemon.heldItem().is(requiredGem);
    }

    @Nullable
    public static Item getRequiredMegaGem(Pokemon pokemon) {
        MegaEvolution megaEvolution = getPossibleMegaEvolution(pokemon);
        if (megaEvolution == null) {
            return null;
        }

        return MegaEvolutionItems.MEGA_GEMS.get(megaEvolution);
    }

    public static Boolean hasRequiredFlower(Pokemon pokemon) {
        if (pokemon.getSpecies().toString().equals("floette")) {
            return pokemon.getAspects().contains("flower-eternal");
        }

        return true;
    }

    @Nullable
    public static MegaEvolution getPossibleMegaEvolution(Pokemon pokemon) {
        ItemStack heldItem = pokemon.heldItem();
        List<MegaEvolution> megaEvolutions = MegaEvolution.fromPokemon(pokemon);

        for (MegaEvolution megaEvolution : megaEvolutions) {
            if (megaEvolution.hasGem()) {
                Item megaGem = MegaEvolutionItems.MEGA_GEMS.get(megaEvolution);
                if (heldItem.is(megaGem)) {
                    return megaEvolution;
                }
            }

            if (hasRequiredMegaMove(pokemon, megaEvolution)) {
                return megaEvolution;
            }
        }

        return null;
    }

    public static boolean hasRequiredMegaMove(Pokemon pokemon) {
        List<String> megaMoves = getRequiredMegaMoveNames(pokemon);
        return hasRequiredMegaMove(pokemon, megaMoves);
    }

    public static boolean hasRequiredMegaMove(Pokemon pokemon, MegaEvolution megaEvolution) {
        String requiredMove = getRequiredMegaMoveName(pokemon, megaEvolution);
        if (requiredMove == null) {
            return false;
        }

        return hasRequiredMegaMove(pokemon, List.of(requiredMove));
    }

    public static boolean hasRequiredMegaMove(Pokemon pokemon, List<String> requiredMoves) {
        List<String> moveNames = pokemon.getMoveSet().getMoves().stream()
                .map(Move::getName)
                .toList();

        return !Collections.disjoint(requiredMoves, moveNames);
    }

    @Nullable
    public static String getRequiredMegaMoveName(Pokemon pokemon, MegaEvolution megaEvolution) {
        FormData formData = getMegaForm(pokemon, megaEvolution);

        if (formData == null) {
            return null;
        }

        return formData.getRequiredMove();
    }

    public static List<String> getRequiredMegaMoveNames(Pokemon pokemon) {
        List<FormData> megaForms = getMegaForms(pokemon);

        return megaForms.stream()
                .map(FormData::getRequiredMove)
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<FormData> getMegaForms(Pokemon pokemon) {
        List<FormData> megaForms = new ArrayList<>();

        for (FormData formData : pokemon.getSpecies().getForms()) {
            if (isMegaForm(formData)) {
                megaForms.add(formData);
            }
        }

        return megaForms;
    }

    @Nullable
    public static FormData getMegaForm(Pokemon pokemon, MegaEvolution megaEvolution) {
        for (FormData formData : pokemon.getSpecies().getForms()) {
            if (isMegaFormOf(formData, megaEvolution)) {
                return formData;
            }
        }

        return null;
    }

    public static Map<FormData, String> getFormToRequiredMegaMove(Pokemon pokemon) {
        List<FormData> megaForms = getMegaForms(pokemon);
        Map<FormData, String> formToMove = new HashMap<>();

        for (FormData formData : megaForms) {
            String moveName = formData.getRequiredMove();

            if (moveName != null) {
                formToMove.put(formData, moveName);
            }
        }

        return formToMove;
    }

    public static boolean hasMegaPokemon(ServerPlayer player) {
        return hasMegaPokemonInParty(player) || hasMegaPokemonInPC(player);
    }

    public static boolean hasMegaPokemonInParty(ServerPlayer player) {
        return hasPokemonInPartyWithPredicate(player, MegaEvolutionUtils::isMegaPokemon);
    }

    public static boolean hasMegaPokemonInPC(ServerPlayer player) {
        return hasPokemonInPCWithPredicate(player, MegaEvolutionUtils::isMegaPokemon);
    }

    public static boolean isMegaPokemon(Pokemon pokemon) {
        Set<String> pokemonAspects = pokemon.getAspects();
        return intersectsMegaAspects(pokemonAspects);
    }

    public static boolean isMegaForm(FormData data) {
        Set<String> formAspects = new HashSet<>(data.getAspects());
        return intersectsMegaAspects(formAspects);
    }

    public static boolean isMegaFormOf(FormData data, MegaEvolution megaEvolution) {
        return data.getAspects().contains(megaEvolution.getMegaAspect());
    }

    public static boolean hasMegaEvolvingPokemonInParty(ServerPlayer player) {
        return hasPokemonInPartyWithPredicate(player, MegaEvolutionUtils::isMegaEvolving);
    }

    public static boolean isMegaEvolving(ServerPlayer player, int slot) {
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        Pokemon pokemon = party.get(slot);
        if (pokemon == null) {
            return false;
        }

        return isMegaEvolving(pokemon);
    }

    public static boolean isMegaEvolving(ServerPlayer player, UUID pokemonUUID) {
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        Pokemon pokemon = party.get(pokemonUUID);
        if (pokemon == null) {
            return false;
        }

        return isMegaEvolving(pokemon);
    }

    public static boolean isMegaEvolving(Pokemon pokemon) {
        PokemonEntity pokemonEntity = pokemon.getEntity();
        if (pokemonEntity == null) {
            return false;
        }

        return isMegaEvolving(pokemonEntity);
    }

    public static boolean isMegaEvolving(PokemonEntity pokemonEntity) {
        if (pokemonEntity instanceof IMegaPokemonEntity megaPokemonEntity) {
            return megaPokemonEntity.isMegaEvolving();
        }

        return false;
    }

    public static boolean isMegaEvolvedInBattle(Pokemon pokemon) {
        if (pokemon instanceof IMegaPokemon megaPokemon) {
            return megaPokemon.isMegaEvolvedInBattle();
        }

        return false;
    }

    private static boolean intersectsMegaAspects(Set<String> pokemonAspects) {
        Set<String> megaAspects = MegaEvolution.getMegaAspects();
        return !Collections.disjoint(pokemonAspects, megaAspects);
    }

    public static boolean hasPokemonInPartyWithPredicate(ServerPlayer player, Predicate<Pokemon> predicate) {
        PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
        PlayerPartyStore party = storage.getParty(player);

        return hasPokemonWithPredicate(party, predicate);
    }

    public static boolean hasPokemonInPCWithPredicate(ServerPlayer player, Predicate<Pokemon> predicate) {
        PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
        PCStore pc = storage.getPC(player);

        return hasPokemonWithPredicate(pc, predicate);
    }

    public static <T extends StorePosition> boolean hasPokemonWithPredicate(PokemonStore<T> pokemonStore, Predicate<Pokemon> predicate) {
        for (Pokemon pokemon : pokemonStore) {
            if (predicate.test(pokemon)) {
                return true;
            }
        }

        return false;
    }

    public static boolean holdsMegaBracelet(ServerPlayer player) {
        return player.isHolding(MegaEvolutionItems.MEGA_BRACELET);
    }

    public static boolean hasMegaBracelet(ServerPlayer player) {
        return player.getInventory()
                .contains(MegaEvolutionItems.MEGA_BRACELET.getDefaultInstance());
    }

}
