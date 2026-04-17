package gg.mmorealms.module.catch_combo.backend.fabric.database;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.catch_combo.backend.fabric.CatchComboFabricModule;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import jakarta.validation.constraints.NotNull;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface ICatchCombo extends ISavable {

    static @NotNull ICatchCombo get(IUser user) {
        return get(user.getUUID());
    }

    static ICatchCombo get(ServerPlayer player) {
        return get(player.getUUID());
    }

    static ICatchCombo get(UUID uuid) {
        ICatchCombo catchCombo = CatchComboFabricModule.instance().getCatchComboDatabaseLoader().getByIdentifier(uuid);

        if (catchCombo == null) {
            Logger.error("Failed to automatically create CatchCombo for user with UUID: " + uuid);
            return new CatchCombo(uuid);
        }

        return catchCombo;
    }

    int getCombo();

    String getSpeciesName();

    String getSpeciesDisplayName();

    boolean hasCombo();

    boolean isSameSpecies(Pokemon pokemon);

    boolean isSameSpecies(String pokemonSpecies);

    float getSpawnRateMultiplier();

    float getShinyRateBonus();

    boolean tryIncrementCombo(Pokemon pokemon);

    void reset();

    void resetAndNotifyIfHasCombo(ServerPlayer player, MessageBuilder messageBuilder);

    void resetAndNotifyIfHasCombo(UUID playerUUID, MessageBuilder messageBuilder);

    MessageBuilder parseComboMessage(MessageBuilder messageBuilder);

}
