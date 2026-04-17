package gg.mmorealms.module.mega_evolution.backend.fabric.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.util.Set;

public class MegaEvolutionConfig {

    public int megaEvolutionAnimationPacketDistance = 128;

    public Set<String> megaEvolutionAnimation = Set.of("q.bedrock_stateful('mega_evolution', 'mega_evolution', 'endures_primary_animations');");
    // Use same value as in animation json file
    public Time megaEvolutionAnimationDuration = Time.seconds(5);
    // When to change form of pokemon to mega while playing animation
    public Time megaEvolutionFormChangeDelay = Time.seconds(3);

    public Lang lang = new Lang();

    public static class Lang {
        public MessageBuilder cantChallengeMessage = new MessageBuilder("<red>Can't start battle while mega evolving");
        public MessageBuilder cantCallbackMessage = new MessageBuilder("<red>Can't callback while mega evolving");
        public MessageBuilder cantEvolveMessage = new MessageBuilder("<red>Can't evolve while mega evolving");
        public MessageBuilder cantTradeMessage = new MessageBuilder("<red>You can't trade Mega Evolved Pokemon");
        public MessageBuilder noRequiredMegaMove = new MessageBuilder("<red>This Pokemon does not have required mega move in its moveset");
        public MessageBuilder noRequiredMegaGem = new MessageBuilder("<red>This Pokemon does not hold required Mega Gem");
        public MessageBuilder megaEvolvingPokemonMessage = new MessageBuilder("<red>You already have mega evolving Pokemon");
        public MessageBuilder megaPokemonInPartyMessage = new MessageBuilder("<red>You already have Mega Pokemon in your party");
        public MessageBuilder megaPokemonInPCMessage = new MessageBuilder("<red>You already have Mega Pokemon in your PC");
        public MessageBuilder megaEvolutionStartBattleMessage = new MessageBuilder("{pokemon} begins mega evolving!");
        public MessageBuilder megaEvolutionFloetteFlowerMessage = new MessageBuilder("<red>Floette must have Eternal Flower to mega evolve");
    }

}
