package gg.mmorealms.module.core.backend.common.dto.cooldown;

import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.common.dto.cooldowns.CommonCooldowns;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "backend_cooldowns")
@NoArgsConstructor
public class BackendCooldowns extends CommonCooldowns implements IBackendCooldowns {

	public BackendCooldowns(UUID uuid) {
		super(uuid);
	}

	@Override
	@Transient
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return CoreBackendModule.instance().getCooldownsLoader();
	}
}
