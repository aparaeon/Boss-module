package gg.mmorealms.module.catch_combo.backend.fabric.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

public class CatchComboConfig {

    public int maxCombo = 60;
    public float spawRateComboMultiplier = 0.083f;
    public float shinyRateComboMultiplier = 0.003f;

    // Max 6
    public List<Integer> perfectIVThresholds = new ArrayList<>() {{
        add(20);
        add(40);
        add(60);
    }};

    public Lang lang = new Lang();

    public static class Lang {
        public MessageBuilder comboCommandMessage = new MessageBuilder("<green>You have caught {combo} {species} in a row!");
        public MessageBuilder noComboCommandMessage = new MessageBuilder("<red>You have no active combo");
        public MessageBuilder comboCorrectPokemon = new MessageBuilder("<green>You have caught {combo} {species} in a row!");
        public MessageBuilder comboWrongPokemon = new MessageBuilder("<red>You caught a {species}. Your {oldSpecies} combo has ended");
        public MessageBuilder comboPokemonFainted = new MessageBuilder("<red>{species} fainted. Your combo has ended");
        public MessageBuilder comboBattleLost = new MessageBuilder("<red>You lost a battle. Your combo has ended");
        public MessageBuilder comboBattleFled = new MessageBuilder("<red>You ran from battle! Your combo has ended");
    }

}
