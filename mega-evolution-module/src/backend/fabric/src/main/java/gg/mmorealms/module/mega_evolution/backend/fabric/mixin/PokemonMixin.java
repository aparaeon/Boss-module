package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;


import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IMegaPokemon;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Pokemon.class)
public class PokemonMixin implements IMegaPokemon {

    @Unique
    @Getter
    @Setter
    private boolean isMegaEvolvedInBattle = false;

}
