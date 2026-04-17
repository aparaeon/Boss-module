package gg.mmorealms.module.catch_combo.backend.fabric.registry;

import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory;
import gg.mmorealms.module.catch_combo.backend.fabric.manager.CatchComboSpawningInfluence;

public class CatchComboSpawningInfluenceRegistry {

    private CatchComboSpawningInfluenceRegistry() { }

    public static void register() {
        PlayerSpawnerFactory.INSTANCE.getInfluenceBuilders().add(CatchComboSpawningInfluence::new);
    }

}
