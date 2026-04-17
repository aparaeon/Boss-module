package gg.mmorealms.module.breeding.backend.fabric.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.dto.BreedingPair;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingComponents;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class PokemonEggUtils {

    private PokemonEggUtils() {
    }

    public static List<String> EGG_CYCLE_BOOST_ABILITIES = List.of(
            "flamebody",
            "magmaarmor",
            "stemengine"
    );

    public static boolean hasEggCycleBoostAbility(ServerPlayer player) {
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        return hasEggCycleBoostAbility(party);
    }

    public static boolean hasEggCycleBoostAbility(PlayerPartyStore party) {
        for (Pokemon pokemon : party) {
            if (PokemonEggUtils.isEggCycleBoostAbility(pokemon)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEggCycleBoostAbility(Pokemon pokemon) {
        return EGG_CYCLE_BOOST_ABILITIES.contains(pokemon.getAbility().getName());
    }

    public static ItemStack randomEgg() {
        return generateEgg(new Pokemon(), new Pokemon());
    }

    @Nullable
    public static ItemStack generateEgg(Pokemon pokemon1, Pokemon pokemon2) {
        BreedingPair parents = new BreedingPair(pokemon1, pokemon2);
        return generateEgg(parents);
    }

    @Nullable
    public static ItemStack generateEgg(BreedingPair parents) {
        PokemonProperties offspringProperties = BreedingUtils.getOffspringProperties(parents);
        if (offspringProperties == null) {
            return null;
        }

        ItemStack pokemonEggStack = new ItemStack(BreedingItems.POKEMON_EGG);
        pokemonEggStack.set(BreedingComponents.FATHER, parents.getFatherParentData());
        pokemonEggStack.set(BreedingComponents.MOTHER, parents.getMotherParentData());
        pokemonEggStack.set(BreedingComponents.POKEMON_PROPERTIES, offspringProperties);
        pokemonEggStack.set(BreedingComponents.STEPS_GOAL, calculateDistanceGoal(parents.getOffspringFormData()));

        return pokemonEggStack;
    }

    public static void hatchEgg(ServerPlayer player, ItemStack itemStack) {
        PokemonProperties properties = getPokemonProperties(itemStack);
        if (properties == null) {
            return;
        }

        Pokemon pokemon = createPokemon(itemStack);
        if (pokemon == null) {
            return;
        }

        CobblemonEvents.HATCH_EGG_PRE.post(new HatchEggEvent.Pre[]{
                new HatchEggEvent.Pre(properties, player),
        }, __ -> null);

        Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon);
        Cobblemon.playerDataManager.getPokedexData(player).encounter(pokemon);

        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        CobblemonEvents.HATCH_EGG_POST.post(new HatchEggEvent.Post[]{
                new HatchEggEvent.Post(player, pokemon),
        }, __ -> null);
    }

    @Nullable
    public static Pokemon createPokemon(ItemStack itemStack) {
        if (!itemStack.is(BreedingItems.POKEMON_EGG)) {
            return null;
        }

        PokemonProperties properties = getPokemonProperties(itemStack);
        if (properties == null) {
            return null;
        }

        return BreedingUtils.createPokemon(properties);
    }

    public static ItemStack pokemonEggItemStack() {
        return new ItemStack(BreedingItems.POKEMON_EGG);
    }

    public static boolean canHatch(ItemStack itemStack) {
        return itemStack.getOrDefault(BreedingComponents.STEPS, 0f) >= itemStack.getOrDefault(BreedingComponents.STEPS_GOAL, 1f);
    }

    public static float calculateDistanceGoal(FormData formData) {
        return calculateDistanceGoal(formData.species);
    }

    public static float calculateDistanceGoal(Species species) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        return species.getEggCycles() * config.egg.eggCycleStepCount;
    }

    public static int getAverageIVs(ItemStack itemStack) {
        PokemonProperties properties = getPokemonProperties(itemStack);
        if (properties == null) {
            return 0;
        }

        IVs ivs = properties.getIvs();
        if (ivs == null) {
            return 0;
        }

        int total = 0;
        int count = 0;

        for (Map.Entry<? extends Stat, ? extends Integer> entry : ivs) {
            int value = entry.getValue();
            total += value;
            count++;
        }

        if (count == 0) {
            return 0;
        }

        return total / count;
    }

    @Nullable
    public static PokemonProperties getPokemonProperties(ItemStack itemStack) {
        return itemStack.get(BreedingComponents.POKEMON_PROPERTIES);
    }

}
