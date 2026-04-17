package gg.mmorealms.module.catch_combo.backend.fabric.manager;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.catch_combo.backend.fabric.CatchComboFabricModule;
import gg.mmorealms.module.catch_combo.backend.fabric.database.ICatchCombo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CatchComboSpawningInfluence implements SpawningInfluence {
    // Get everything dynamically, as influence gets initialised only once
    private final ServerPlayer player;

    public CatchComboSpawningInfluence(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public float affectWeight(@NotNull SpawnDetail spawnDetail, @NotNull SpawnablePosition spawnablePosition, float weight) {
        ICatchCombo catchCombo = getCatchCombo();

        if (catchCombo.getSpeciesName().isEmpty()) {
            return weight;
        }

        if (!isTargetPokemon(spawnDetail.getName().getString().toLowerCase())) {
            return weight;
        }

        return weight * (1 + catchCombo.getSpawnRateMultiplier());
    }

    @Override
    public void affectSpawn(@NotNull SpawnAction<?> action, @NotNull Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        if (!isTargetPokemon(pokemonEntity.getPokemon().getSpecies().toString().toLowerCase())) {
            return;
        }

        int perfectIVCount = getPerfectIVCount();
        if (perfectIVCount <= 0) {
            return;
        }

        List<Stat> stats = new ArrayList<>(Stats.Companion.getPERMANENT().stream().toList());
        Collections.shuffle(stats);

        perfectIVCount = Math.min(perfectIVCount, Stats.Companion.getPERMANENT().size());
        Set<Stat> perfectStats = new HashSet<>(stats.subList(0, perfectIVCount));

        for (Stat stat : perfectStats) {
            pokemonEntity.getPokemon().setIV(stat, 31);
        }
    }

    private ICatchCombo getCatchCombo() {
        return ICatchCombo.get(this.player);
    }

    private boolean isTargetPokemon(String name) {
        return getCatchCombo().getSpeciesName().contains(name);
    }

    private int getPerfectIVCount() {
        ICatchCombo catchCombo = getCatchCombo();

        int perfectIVCount = 0;
        List<Integer> ivThresholds = CatchComboFabricModule.instance().getConfig().perfectIVThresholds;

        if (ivThresholds == null || ivThresholds.isEmpty()) {
            Logger.warn("IV thresholds not configured");
            return 0;
        }

        for (int ivThreshold : ivThresholds) {
            if (catchCombo.getCombo() < ivThreshold) {
                break;
            }

            perfectIVCount++;
        }

        return perfectIVCount;
    }
}
