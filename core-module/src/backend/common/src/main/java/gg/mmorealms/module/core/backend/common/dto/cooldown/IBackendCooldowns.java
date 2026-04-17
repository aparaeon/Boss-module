package gg.mmorealms.module.core.backend.common.dto.cooldown;

import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.common.dto.cooldowns.ICommonCooldowns;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IBackendCooldowns extends ICommonCooldowns {

	static @NotNull IBackendCooldowns getByUser(IUser user) {
		IBackendCooldowns cooldowns = getByUUID(user.getUUID());

		if (cooldowns == null) {
			cooldowns = new BackendCooldowns(user.getUUID());
		}

		return cooldowns;
	}

	static @NotNull IBackendCooldowns getByPlayer(ServerPlayer player) {
		IBackendCooldowns cooldowns = getByUUID(player.getUUID());

		if (cooldowns == null) {
			cooldowns = new BackendCooldowns(player.getUUID());
		}

		return cooldowns;
	}

	static @Nullable IBackendCooldowns getByUUID(@NotNull UUID uuid) {
		return CoreBackendModule.instance().getCooldownsLoader().getByIdentifier(uuid);
	}

}
