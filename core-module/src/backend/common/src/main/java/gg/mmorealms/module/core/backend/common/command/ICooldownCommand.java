package gg.mmorealms.module.core.backend.common.command;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import org.jetbrains.annotations.NotNull;

public interface ICooldownCommand {

	String getCOMMAND_COOLDOWN_KEY();

	default boolean isCommandOnCooldown(@NotNull User user) {
		CoreConfig coreConfig = CoreBackendModule.instance().getConfig();
		String key = getCOMMAND_COOLDOWN_KEY();

		if (user.hasCooldown(key)) {
			Time cooldown = user.getCooldown(key);
			MessageBuilder messageBuilder = coreConfig.lang.cooldown
					.parse("cooldown", cooldown);

			user.sendMessage(messageBuilder);
			return true;
		}

		return false;
	}

	default void setCommandCooldown(@NotNull User user, Time time) {
		user.setCooldown(getCOMMAND_COOLDOWN_KEY(), time);
	}

}
