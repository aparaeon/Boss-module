package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;

import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(value = PokemonSpecies.ShowdownSpecies.class)
public class ShowdownSpeciesMixin {

    @Shadow
    @Final
    @Mutable
    private Map<String, String> abilities;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void populateAbilities(Species species, FormData formData, CallbackInfo ci) {
        mega_evolution$populateAbilities(formData);
    }

    @Unique
    private void mega_evolution$populateAbilities(FormData formData) {
        if (formData == null) {
            return;
        }

        Iterator<PotentialAbility> abilityIterator = formData.getAbilities().iterator();

        abilities = Map.of("0", abilityIterator.hasNext()
                ? abilityIterator.next().getTemplate().getName()
                : "No Ability");
    }
}
