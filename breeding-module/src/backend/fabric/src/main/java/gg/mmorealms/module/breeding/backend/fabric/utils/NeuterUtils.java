package gg.mmorealms.module.breeding.backend.fabric.utils;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class NeuterUtils {

    private NeuterUtils() {
    }

    @Nullable
    public static Boolean toggleNeutered(User user, Pokemon pokemon) {
        return setNeutered(user, pokemon, !isNeutered(pokemon));
    }

    @Nullable
    public static Boolean setNeutered(User user, Pokemon pokemon, boolean neutered) {
        BreedingConfig.Lang lang = BreedingFabricModule.instance().getConfig().lang;

        Boolean isNeutered = setNeutered(user.getPlayer(), pokemon, neutered);

        MessageBuilder messageBuilder;

        if (isNeutered == null) {
            messageBuilder = lang.originalTrainerNeuterMessage;
        } else if (isNeutered) {
            messageBuilder = lang.neuteredMessage;
        } else {
            messageBuilder = lang.unneuteredMessage;
        }

        messageBuilder = messageBuilder
                .parse("pokemonName", pokemon.getDisplayName(false).getString());

        user.sendMessage(messageBuilder);

        return isNeutered;
    }

    @Nullable
    public static Boolean setNeutered(ServerPlayer player, Pokemon pokemon, boolean neutered) {
        String originalTrainerUUID = pokemon.getOriginalTrainer();
        String playerUUID = player.getStringUUID();

        if (!playerUUID.equals(originalTrainerUUID)) {
            return null;
        }

        Set<String> aspects = new HashSet<>(pokemon.getForcedAspects());

        if (neutered) {
            aspects.add(IPokemon.NEUTERED_KEY);
        } else {
            aspects.remove(IPokemon.NEUTERED_KEY);
        }

        pokemon.setForcedAspects(aspects);

        return neutered;
    }

    public static boolean isNeutered(Pokemon pokemon) {
        return pokemon.getForcedAspects().contains(IPokemon.NEUTERED_KEY);
    }

}
