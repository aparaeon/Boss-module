package gg.mmorealms.module.catch_combo.backend.fabric.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.catch_combo.backend.fabric.database.CatchCombo;
import gg.mmorealms.module.catch_combo.backend.fabric.database.ICatchCombo;
import gg.mmorealms.module.catch_combo.backend.fabric.database.RemoteCatchCombo;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CatchComboDatabaseLoader extends BackendPlayerDependentDatabaseLoader<ICatchCombo, CatchCombo, RemoteCatchCombo> {

    public CatchComboDatabaseLoader() {
        super(ICatchCombo.class, CatchCombo.class, RemoteCatchCombo.class);
    }

    @Override
    public void onJoin(@NotNull ServerPlayer serverPlayer) {

    }

    @Override
    public void onLeave(@NotNull ServerPlayer serverPlayer) {

    }

    @Override
    protected @NotNull RemoteCatchCombo createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
        return new RemoteCatchCombo(uuid, server);
    }

    @Override
    protected @Nullable CatchCombo createObject(@NotNull UUID uuid) {
        return new CatchCombo(uuid);
    }

}
