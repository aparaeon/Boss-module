package gg.mmorealms.module.boss.common;

/**
 * How loud a boss spawn / defeat announcement should be.
 * <ul>
 *   <li>{@link #OFF} — silent, no announcement.</li>
 *   <li>{@link #WORLD_CHAT} — backend-local chat: only players in the boss's dimension see it.
 *       Used for COMMON/UNCOMMON so the proxy isn't spammed.</li>
 *   <li>{@link #GLOBAL_CHAT} — proxy-wide chat broadcast via the chat-module
 *       {@code GlobalMessageEvent}. Used for RARE/ULTRA_RARE.</li>
 *   <li>{@link #TITLE} — global chat + Adventure {@code Title} + sound for every connected
 *       player. Used for LEGENDARY/MEGA/MYTHICAL.</li>
 * </ul>
 */
public enum AnnounceLevel {
	OFF,
	WORLD_CHAT,
	GLOBAL_CHAT,
	TITLE
}
