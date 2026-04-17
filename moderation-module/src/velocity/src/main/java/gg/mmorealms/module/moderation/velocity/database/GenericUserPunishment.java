package gg.mmorealms.module.moderation.velocity.database;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class GenericUserPunishment implements IDatabaseEntry<Long>, Comparable<GenericUserPunishment> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public Type type;

	private UUID user;
	private UUID issuedBy;

	private String reason;
	private long startTime;

	@JdbcTypeCode(SqlTypes.JSON)
	private Time duration;

	private @Setter boolean active;

	public GenericUserPunishment(Type type, UUID user, UUID issuedBy, String reason, Time duration) {
		this.type = type;
		this.user = user;
		this.issuedBy = issuedBy;
		this.reason = reason;
		this.duration = duration;
		this.startTime = System.currentTimeMillis();
		this.active = true;
	}

	@Override
	public Long getIdentifier() {
		return id;
	}

	@Override
	public DatabaseLoader<Long, ?, ?> getLoader() {
		return null;
	}

	public enum Type {
		BAN,
		MUTE
	}

	public boolean isActive() {
		boolean flag1 = startTime + duration.toMilliseconds() > System.currentTimeMillis();
		return flag1 && active;
	}

	public long getRemainingTime() {
		return startTime + duration.toMilliseconds() - System.currentTimeMillis();
	}

	public String getPrettyRemainingTime() {
		return Time.milliseconds(getRemainingTime()).toString();
	}

	@Override
	public int compareTo(@NotNull GenericUserPunishment other) {
		return Long.compare(this.startTime, other.startTime);
	}

	@Override
	public String toString() {
		return parse(getUserMessageTemplate());
	}

	public String toShortString() {
		return parse(getShortEntry());
	}

	private String parse(MessageBuilder messageBuilder) {
		return messageBuilder
				.parse("user", MojangUtils.getUsername(this.user))
				.parse("staff", MojangUtils.getUsername(this.issuedBy))
				.parse("reason", this.reason)
				.parse("duration", this.duration.toString())
				.parse("remaining", Time.milliseconds(getRemainingTime()).toString())
				.parse("date", new SimpleDateFormat("dd-MM-yy").format(new Date(this.startTime)))
				.parse("active", this.isActive() ?
						ModerationVelocityModule.instance().getConfig().lang.active :
						ModerationVelocityModule.instance().getConfig().lang.inactive
				)
				.parse();

	}

	protected abstract MessageBuilder getUserMessageTemplate();

	protected abstract MessageBuilder getShortEntry();
}
