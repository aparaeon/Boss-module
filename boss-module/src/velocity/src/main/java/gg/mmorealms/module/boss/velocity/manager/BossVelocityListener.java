package gg.mmorealms.module.boss.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.module.boss.common.event.BossTitleAnnouncementEvent;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

/**
 * Velocity-side listener for boss network events.
 * <p>
 * Currently handles {@link BossTitleAnnouncementEvent} — the TITLE-level announcement tier
 * (LEGENDARY / MEGA / MYTHICAL spawns + any tier admin opts in to TITLE-mode). Renders a
 * MiniMessage chat broadcast, an Adventure {@link Title}, and an optional sound to every
 * connected player.
 */
public class BossVelocityListener {

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ProxyServer proxy;

	@EventHandler
	public void onBossTitleAnnouncement(BossTitleAnnouncementEvent event) {
		// 1. Chat broadcast — route via chat-module's MessageManager so any future chat
		// formatting / hooks (mute checks, etc.) apply consistently with /broadcast.
		if (!event.getChatMessage().isEmpty()) {
			ChatVelocityModule.instance().getMessageManager().sendGlobalMessage(event.getChatMessage());
		}

		// 2. Title + subtitle — fade-in 0.5s, stay 4s, fade-out 1s. Matches AlertManager cadence.
		Component title = miniMessageManager.parse(event.getTitle());
		Component subtitle = miniMessageManager.parse(event.getSubtitle());
		Title kyoriTitle = Title.title(
				title,
				subtitle,
				Title.Times.times(
						Duration.ofMillis(500),
						Duration.ofSeconds(4),
						Duration.ofSeconds(1)
				)
		);
		proxy.showTitle(kyoriTitle);

		// 3. Sound — play to every connected player. Parsing the resource id can fail if the
		// admin types a malformed key; log and skip instead of erroring out the announce path.
		String soundId = event.getSoundId();
		if (soundId != null && !soundId.isEmpty()) {
			try {
				Sound sound = Sound.sound(Key.key(soundId), Sound.Source.MASTER, 1.0F, 1.0F);
				proxy.playSound(sound);
			} catch (Throwable t) {
				Logger.warn("Boss title announce: bad sound id '" + soundId + "': " + t.getMessage());
			}
		}
	}
}
