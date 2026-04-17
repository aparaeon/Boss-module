package gg.mmorealms.module.hunts.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import gg.mmorealms.module.hunts.backend.common.dto.database.IHunts;
import gg.mmorealms.module.hunts.backend.common.dto.database.RemoteHunts;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HuntsDatabaseLoader extends BackendPlayerDependentDatabaseLoader<IHunts, Hunts, RemoteHunts> {

    public HuntsDatabaseLoader() {
        super(IHunts.class, Hunts.class, RemoteHunts.class);
    }

    @Override
    public void onJoin(@NotNull ServerPlayer serverPlayer) {

    }

    @Override
    public void onLeave(@NotNull ServerPlayer serverPlayer) {

    }

    @Override
    protected @NotNull RemoteHunts createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
        return new RemoteHunts(uuid, server);
    }

    @Override
    protected @Nullable Hunts createObject(@NotNull UUID uuid) {
        return new Hunts(uuid);
    }

}
