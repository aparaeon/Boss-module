package gg.mmorealms.module.catch_combo.backend.fabric.database;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.catch_combo.backend.fabric.CatchComboFabricModule;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

@Entity(name="catch_combo")
@NoArgsConstructor
@Getter
@Setter
public class CatchCombo implements ICatchCombo, IDatabaseEntry<UUID> {

    @Id
    @NotNull
    private UUID uuid;

    private String speciesName = "";
    private String speciesDisplayName = "";

    private int combo = 0;

    public CatchCombo(UUID uuid) {
        this.uuid = uuid;
    }

    public CatchCombo(ServerPlayer player) {
        this(player.getUUID());
    }

    @Override
    public boolean hasCombo() {
        return combo >= 2;
    }

    @Override
    public boolean isSameSpecies(Pokemon pokemon) {
        return isSameSpecies(pokemon.getSpecies().toString());
    }

    @Override
    public boolean isSameSpecies(String pokemonSpecies) {
        return this.speciesName.equals(pokemonSpecies);
    }

    @Override
    public int getCombo() {
        return clamp(this.combo);
    }

    private int clamp(int combo) {
        int maxCombo = CatchComboFabricModule.instance().getConfig().maxCombo;
        return Math.clamp(combo, 0, maxCombo);
    }

    private void setCombo(int combo) {
        this.combo = clamp(combo);
    }

    @Override
    public float getSpawnRateMultiplier() {
        return calculateBonus(CatchComboFabricModule.instance().getConfig().spawRateComboMultiplier);
    }

    @Override
    public float getShinyRateBonus() {
        return calculateBonus(CatchComboFabricModule.instance().getConfig().shinyRateComboMultiplier);
    }

    private float calculateBonus(float multiplier) {
        if (!hasCombo()) {
            return 0;
        }

        return Math.max(0, this.combo) * multiplier;
    }

    @Override
    public boolean tryIncrementCombo(Pokemon pokemon) {
        boolean isSameSpecies = isSameSpecies(pokemon);

        if (isSameSpecies) {
            setCombo(this.combo + 1);
        } else {
            setSpeciesName(pokemon);
            this.combo = 1;
        }

        try {
            save();
            return isSameSpecies;
        } catch (DatabaseSaveException e) {
            Logger.error(new MessageBuilder("{uuid} tried to increment catch combo but there was a save exception")
                    .parse("uuid", uuid));
            return false;
        }
    }

    private void setSpeciesName(Pokemon pokemon) {
        Species species = pokemon.getSpecies();

        this.speciesName = species.toString();
        this.speciesDisplayName = species.getName();
    }

    private void resetPokemonSpecies() {
        this.speciesName = "";
        this.speciesDisplayName = "";
    }

    @Override
    public void reset() {
        this.combo = 0;
        resetPokemonSpecies();

        try {
            save();
        } catch (DatabaseSaveException e) {
            Logger.error(new MessageBuilder("{uuid} tried to reset data but there was a save exception")
                    .parse("uuid", uuid));
        }
    }

    @Override
    public void resetAndNotifyIfHasCombo(ServerPlayer player, MessageBuilder messageBuilder) {
        resetAndNotifyIfHasCombo(player.getUUID(), messageBuilder);
    }

    @Override
    public void resetAndNotifyIfHasCombo(UUID playerUUID, MessageBuilder messageBuilder) {
        if (hasCombo()) {
            MessageBuilder message = parseComboMessage(messageBuilder);
            IUser user = IUser.getByUUID(playerUUID);
            user.sendMessage(message);
        }

        reset();
    }

    @Override
    public MessageBuilder parseComboMessage(MessageBuilder messageBuilder) {
        return messageBuilder
                .parse("combo", this.combo)
                .parse("maxCombo", CatchComboFabricModule.instance().getConfig().maxCombo)
                .parse("species", speciesDisplayName);
    }

    @Override
    public UUID getIdentifier() {
        return uuid;
    }

    @Override
    public SyncedDatabaseLoader<UUID, ?, ?, ?> getLoader() {
        return CatchComboFabricModule.instance().getCatchComboDatabaseLoader();
    }

}
