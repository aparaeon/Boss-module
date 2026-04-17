package gg.mmorealms.module.breeding.backend.fabric.dto;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.breeding.backend.fabric.utils.BreedingUtils;
import gg.mmorealms.module.breeding.backend.fabric.utils.PokemonEggUtils;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class BreedingPair {

    private final Pokemon father;
    private final Pokemon mother;
    private final FormData offspringFormData;

    private final boolean isCompatible;

    private record ParentRole(Pokemon father, Pokemon mother) {
    }

    public BreedingPair(Pokemon pokemon1, Pokemon pokemon2) {
        ParentRole roles = assignRoles(pokemon1, pokemon2);
        this.father = roles.father();
        this.mother = roles.mother();
        this.offspringFormData = BreedingUtils.getOffspringFormData(father, mother);

        this.isCompatible = BreedingUtils.isCompatiblePair(this.father, this.mother);
    }

    /**
     * Assigns father/mother roles to Pokemons based on gender and whether they are Ditto
     */
    private static ParentRole assignRoles(Pokemon pokemon1, Pokemon pokemon2) {
        Gender gender1 = pokemon1.getGender();
        Gender gender2 = pokemon2.getGender();

        if (gender1 == Gender.MALE && gender2 == Gender.FEMALE) {
            return new ParentRole(pokemon1, pokemon2);
        }

        if (gender1 == Gender.FEMALE && gender2 == Gender.MALE) {
            return new ParentRole(pokemon2, pokemon1);
        }

        if (BreedingUtils.isDitto(pokemon2)) {
            return new ParentRole(pokemon2, pokemon1);
        }

        return new ParentRole(pokemon1, pokemon2);
    }

    @Nullable
    public ItemStack breedEgg() {
        return PokemonEggUtils.generateEgg(this);
    }

    @Nullable
    public Pokemon breedPokemon() {
        return BreedingUtils.breed(this);
    }

    public boolean hasItem(Item item) {
        return toList().stream().anyMatch(pokemon -> pokemon.heldItem().is(item));
    }

    public ParentData getFatherParentData() {
        return BreedingUtils.toParentData(father);
    }

    public ParentData getMotherParentData() {
        return BreedingUtils.toParentData(mother);
    }

    public List<ParentData> toParentData() {
        return List.of(getFatherParentData(), getMotherParentData());
    }

    public Set<MoveTemplate> fatherAllMoveTemplates() {
        return getAllMoveTemplates(father);
    }

    public Set<MoveTemplate> motherAllMoveTemplates() {
        return getAllMoveTemplates(mother);
    }

    private Set<MoveTemplate> getAllMoveTemplates(Pokemon pokemon) {
        Set<MoveTemplate> allAccessibleMoves = pokemon.getAllAccessibleMoves();

        List<MoveTemplate> activeMoves = pokemon.getMoveSet().getMoves().stream()
                .map(Move::getTemplate)
                .toList();

        allAccessibleMoves.addAll(activeMoves);

        return allAccessibleMoves;
    }

    public Set<MoveTemplate> offspringLevelUpMoves() {
        int maxLvl = Math.max(father.getLevel(), mother.getLevel());

        // Get level-up moves the offspring can learn
        Set<MoveTemplate> offspringLevelMoves = new LinkedHashSet<>(
                offspringFormData.getMoves().getLevelUpMovesUpTo(maxLvl));

        // Get level-up moves each parent can learn
        Set<MoveTemplate> fatherLevelUpMoves = father.getForm().getMoves().getLevelUpMovesUpTo(maxLvl);
        Set<MoveTemplate> motherLevelUpMoves = mother.getForm().getMoves().getLevelUpMovesUpTo(maxLvl);

        // Only keep moves that both parents can learn via level-up and the offspring can learn
        offspringLevelMoves.retainAll(fatherLevelUpMoves);
        offspringLevelMoves.retainAll(motherLevelUpMoves);

        return offspringLevelMoves;
    }

    public Set<MoveTemplate> commonMoves() {
        Set<MoveTemplate> commonMoves = new LinkedHashSet<>(fatherAllMoveTemplates());
        commonMoves.retainAll(motherAllMoveTemplates());

        return commonMoves;
    }

    public Set<MoveTemplate> offspringEggMoves() {
        Set<MoveTemplate> eggPool = Set.copyOf(offspringFormData.species.getMoves().getEggMoves());

        return Stream.concat(fatherAllMoveTemplates().stream(), motherAllMoveTemplates().stream())
                .filter(eggPool::contains)
                .collect(Collectors.toSet());
    }

    public List<Pokemon> toList() {
        return List.of(father, mother);
    }

    public void forEach(Consumer<Pokemon> consumer) {
        toList().forEach(consumer);
    }

    public Pokemon random() {
        return random(ThreadLocalRandom.current());
    }

    public Pokemon random(Random random) {
        return random.nextBoolean() ? father : mother;
    }
}

