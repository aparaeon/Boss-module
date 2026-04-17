package gg.mmorealms.module.catch_combo.backend.fabric.database;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class RemoteCatchCombo extends UUIDRemoteObject<ICatchCombo> implements ICatchCombo {

    public RemoteCatchCombo(@NotNull UUID uuid, @NotNull String server) {
        super(ICatchCombo.class, uuid, server);
    }

    @Override
    public int getCombo() {
        return sendRequest();
    }

    @Override
    public String getSpeciesName() {
        return sendRequest();
    }

    @Override
    public String getSpeciesDisplayName() {
        return sendRequest();
    }

    @Override
    public boolean hasCombo() {
        return sendRequest();
    }

    @Override
    public boolean isSameSpecies(Pokemon pokemon) {
        return sendRequest(pokemon);
    }

    @Override
    public boolean isSameSpecies(String pokemonSpecies) {
        return sendRequest(pokemonSpecies);
    }

    @Override
    public float getSpawnRateMultiplier() {
        return sendRequest();
    }

    @Override
    public float getShinyRateBonus() {
        return sendRequest();
    }

    @Override
    public boolean tryIncrementCombo(Pokemon pokemon) {
        return sendRequest(pokemon);
    }

    @Override
    public void reset() {
        sendRequest();
    }

    @Override
    public void resetAndNotifyIfHasCombo(ServerPlayer player, MessageBuilder messageBuilder) {
        sendRequest(player, messageBuilder);
    }

    @Override
    public void resetAndNotifyIfHasCombo(UUID playerUUID, MessageBuilder messageBuilder) {
        sendRequest(playerUUID, messageBuilder);
    }

    @Override
    public MessageBuilder parseComboMessage(MessageBuilder messageBuilder) {
        return sendRequest(messageBuilder);
    }

    @Override
    public void save() {
        sendRequest();
    }

}
