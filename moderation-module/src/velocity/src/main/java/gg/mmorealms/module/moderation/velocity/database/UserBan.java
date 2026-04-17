package gg.mmorealms.module.moderation.velocity.database;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "bans")
@NoArgsConstructor
public class UserBan extends GenericUserPunishment {

	public UserBan(UUID user, UUID issuedBy, String reason, Time duration) {
		super(Type.BAN, user, issuedBy, reason, duration);
	}

	@Override
	@Transient
	protected MessageBuilder getUserMessageTemplate() {
		return ModerationVelocityModule.instance().getConfig().lang.banMessage;
	}

	@Override
	@Transient
	protected MessageBuilder getShortEntry() {
		return ModerationVelocityModule.instance().getConfig().lang.banEntry;
	}
}
