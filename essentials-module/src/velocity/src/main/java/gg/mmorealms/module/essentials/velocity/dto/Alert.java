package gg.mmorealms.module.essentials.velocity.dto;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;

@Getter
public class Alert {

	private static final MessageBuilder actionBar = new MessageBuilder(
			"<white>{title} <gray>- <yellow>{time} left"
	);

	private final String title;
	private final String description;
	private final long timestamp;
	private final Time duration;

	@Setter
	private BossBar bossBar;

	public Alert(String title, String description, long timestamp, Time duration) {
		this.title = title;
		this.description = description;
		this.timestamp = timestamp;
		this.duration = duration;
	}

	public Time getTimeLeft() {
		return Time.milliseconds(duration.toMilliseconds() - (System.currentTimeMillis() - timestamp));
	}

	public String getActionBar() {
		return actionBar
				.parse("title", title)
				.parse("description", description)
				.parse("time", getTimeLeft().toString())
				.parse();
	}

	public float getRemainingPercentage() {
		return (float) getTimeLeft().toMilliseconds() / duration.toMilliseconds();
	}

}
