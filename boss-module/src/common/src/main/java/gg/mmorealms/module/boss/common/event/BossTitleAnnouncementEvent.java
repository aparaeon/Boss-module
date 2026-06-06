package gg.mmorealms.module.boss.common.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sent from a backend to the proxy when a {@link gg.mmorealms.module.boss.common.AnnounceLevel#TITLE}
 * announcement should fire for every connected player. Velocity-side listener renders the title
 * + subtitle via Adventure, broadcasts the chat line, and plays the sound.
 * <p>
 * All fields are pre-rendered MiniMessage strings — the listener parses, never interprets.
 */
@Getter
public class BossTitleAnnouncementEvent extends NetworkEvent {
	/** MiniMessage chat line (broadcast). May be empty to skip the chat broadcast. */
	private final @NotNull String chatMessage;
	/** MiniMessage title text. */
	private final @NotNull String title;
	/** MiniMessage subtitle text. May be empty. */
	private final @NotNull String subtitle;
	/** Minecraft sound resource id, e.g. {@code minecraft:entity.wither.spawn}. Null/empty = no sound. */
	private final @Nullable String soundId;

	public BossTitleAnnouncementEvent(
			@NotNull String chatMessage,
			@NotNull String title,
			@NotNull String subtitle,
			@Nullable String soundId
	) {
		super();
		this.chatMessage = chatMessage;
		this.title = title;
		this.subtitle = subtitle;
		this.soundId = soundId;
	}
}
