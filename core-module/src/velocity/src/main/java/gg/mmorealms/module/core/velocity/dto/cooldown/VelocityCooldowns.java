package gg.mmorealms.module.core.velocity.dto.cooldown;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.core.common.dto.cooldowns.CommonCooldowns;
import gg.mmorealms.module.core.velocity.CoreVelocityModule;
import gg.mmorealms.module.core.velocity.manager.VelocityCooldownsLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "velocity_cooldowns")
@NoArgsConstructor
public class VelocityCooldowns extends CommonCooldowns {

	public VelocityCooldowns(UUID uuid) {
		super(uuid);
	}

	public static VelocityCooldowns get(UUID uuid) {
		VelocityCooldowns cooldowns = CoreVelocityModule.instance().getVelocityCooldownsLoader().getByIdentifier(uuid);

		if (cooldowns == null) {
			cooldowns = new VelocityCooldowns(uuid);
			try {
				cooldowns.save();
			} catch (DatabaseSaveException e) {
				Logger.error(e);
				return null;
			}
			CoreVelocityModule.instance().getVelocityCooldownsLoader().cache(uuid, cooldowns);
		}

		return cooldowns;
	}

	@Override
	@Transient
	public VelocityCooldownsLoader getLoader() {
		return CoreVelocityModule.instance().getVelocityCooldownsLoader();
	}
}
