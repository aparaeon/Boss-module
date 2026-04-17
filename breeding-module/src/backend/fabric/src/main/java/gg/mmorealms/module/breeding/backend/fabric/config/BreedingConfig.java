package gg.mmorealms.module.breeding.backend.fabric.config;

import com.cobblemon.mod.common.pokemon.Gender;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class BreedingConfig {

    public Time breedingCommandCooldown = Time.hours(1);
    public Time hatchCommandCooldown = Time.hours(1);

    /* ---------- Breeding ---------- */

    public Breeding breeding = new Breeding();

    public static class Breeding {
        public Time breedingAttemptInterval = Time.minutes(1);
        // The value by which breeding chance will increment each failed attempt
        public float breedingChanceBonus = 0.5f;

        // Using separate variable instead of config from Cobblemon
        // for case we want to change cobblemon odds separately
        public int baseShinyOdds = 4096;

        public List<Float> chancePerPairCount = List.of(5f, 6f, 7f, 8f, 8.5f, 9f, 9.5f, 10f);

        // Example:
        // 1 (base roll) + 2 + 6 = 9
        // 4096 / 9 ≈ 1/455 odds
        // Use float for 'rolls' for more precision
        public Map<ShinyMethod, Float> shinyBreedingRolls = Map.of(
                ShinyMethod.CRYSTAL, 2f, // Using odds of Lucky Charm for balance
                ShinyMethod.MASUDA, 6f
        );
    }

    /* ---------- Pasture ---------- */

    public Pasture pasture = new Pasture();

    public static class Pasture {
        public boolean canHopperPickupEgg = true;
        public boolean canPickupEggWithFilledHand = true;

        public boolean hasComparatorSignal = true;
        public int comparatorSignal = Redstone.SIGNAL_MAX;

        public float eggDisplayScale = 0.6f;
        public Vec3 eggDisplayOffset = new Vec3(0f, 0.1f, 0f);
    }

    /* ---------- Egg ---------- */

    public Egg egg = new Egg();

    public static class Egg {
        public int eggCycleStepCount = 128; // Default value form Pokemon games

        public boolean isPlaceable = true;
        public boolean isDispensable = true;
        public boolean hasComparatorSignal = true;

        // Shouldn't be too big, as it tracks distance from previous check position.
        // We want to avoid players moving and backtracking in-between timeframe, without it being accounted
        public Time distanceUpdateInterval = Time.seconds(1);
        public float maxDistancePerUpdate = 10f;

        public Map<Gender, String> genderMap = Map.of(
                Gender.MALE, "<blue>♂",
                Gender.FEMALE, "<red>♀",
                Gender.GENDERLESS, ""
        );

        public String unknownEggName = "???";

        public GUIButton pokemonEggItem = new GUIButton()
                .displayName("{pokemonName} Egg")
                .lore(List.of(
                        "",
                        "--- Parents ---",
                        "{father} {fatherGender}",
                        "{mother} {motherGender}",
                        "---- Steps ----",
                        "Current: {steps}",
                        "Goal: {stepsGoal}",
                        "{hatchInstruction}"
                ));

        public String hatchInstruction = "<green>Shift + Right Click to hatch!";
    }

    public Lang lang = new Lang();

    public static class Lang {
        public MessageBuilder canHatchMessage = new MessageBuilder("<green>You can hatch {pokemonEgg}!");
        public MessageBuilder neuteredMessage = new MessageBuilder("<green>{pokemonName} has been neutered!");
        public MessageBuilder unneuteredMessage = new MessageBuilder("<green>{pokemonName} has been unneutered!");
        public MessageBuilder breedMessage = new MessageBuilder("<green>You got {pokemonEggName}!");

        public String notHoldingPokemonEggMessage = "<red>You need to hold Pokemon Egg which you want to hatch";
        public String samePokemonMessage = "<red>You can't choose same Pokemon";
        public MessageBuilder originalTrainerNeuterMessage = new MessageBuilder("<red>Only original trainer of {pokemonName} can (un)neuter it");
        public MessageBuilder notCompatiblePokemonMessage = new MessageBuilder("<red>{pokemonName1} and {pokemonName2} are not compatible");
    }

}
