package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.MegaInstruction;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.mega_evolution.backend.fabric.MegaEvolutionFabricModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.config.MegaEvolutionConfig;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IMegaPokemon;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = MegaInstruction.class, remap = false)
public class MegaInstructionMixin {

    @Unique
    private final MegaInstruction self = (MegaInstruction) (Object) this;

    @Inject(method = "invoke", at = @At("HEAD"))
    private void onBattleMegaEvolution(PokemonBattle battle, CallbackInfo ci) {
        mega_evolution$onBattleMegaEvolution(battle);
    }

    @Unique
    private void mega_evolution$onBattleMegaEvolution(PokemonBattle battle) {
        BattlePokemon battlePokemon = self.getMessage().battlePokemon(0, battle);
        if (battlePokemon == null) {
            return;
        }

        MegaEvolutionFabricModule module = MegaEvolutionFabricModule.instance();
        MegaEvolutionConfig config = module.getConfig();

        battle.dispatchWaitingToFront(config.megaEvolutionAnimationDuration.toSeconds(), () -> {
            Pokemon pokemon = battlePokemon.getOriginalPokemon();

            if (pokemon instanceof IMegaPokemon megaPokemon) {
                megaPokemon.setMegaEvolvedInBattle(true);
            }

            MegaEvolutionUtils.megaEvolve(pokemon);

            MessageBuilder messageBuilder = config.lang.megaEvolutionStartBattleMessage
                    .parse("pokemon", battlePokemon.getName().getString());
            Component component = module.getMiniMessageManager().parse(messageBuilder);

            battle.broadcastChatMessage(component);

            return Unit.INSTANCE;
        });
    }

}
