package gg.mmorealms.module.essentials.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.dto.Alert;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.title.Title;

import java.time.Duration;

public class AlertManager {

	private final ProxyServer proxy;
	private final VelocityMiniMessageManager miniMessageManager;

	private Alert activeAlert;
	private CancelableTimeTask alertTask;

	public AlertManager() {
		this.proxy = EssentialsVelocityModule.instance().getProxy();
		this.miniMessageManager = EssentialsVelocityModule.instance().getMiniMessageManager();

		alertTask = ScheduleUtils.runTaskTimer(() -> {
			if (activeAlert == null) {
				return;
			}

			proxy.sendActionBar(
					miniMessageManager.parse(activeAlert.getActionBar())
			);

			float remaining = activeAlert.getRemainingPercentage();

			if (remaining <= 0) {
				proxy.hideBossBar(activeAlert.getBossBar());
				activeAlert = null;
				return;
			}

			BossBar bossBar = BossBar.bossBar(
					miniMessageManager.parse(activeAlert.getActionBar()),
					activeAlert.getRemainingPercentage(),
					BossBar.Color.RED,
					BossBar.Overlay.PROGRESS
			);

			if (activeAlert.getBossBar() != null) {
				proxy.hideBossBar(activeAlert.getBossBar());
			}

			proxy.showBossBar(bossBar);
			activeAlert.setBossBar(bossBar);
		}, Time.seconds(1));
	}

	public void registerAlert(Alert alert) {
		if (this.activeAlert != null && this.activeAlert.getBossBar() != null) {
			proxy.hideBossBar(this.activeAlert.getBossBar());
		}

		this.activeAlert = alert;

		if (alert == null) {
			return;
		}

		proxy.showTitle(
				Title.title(
						miniMessageManager.parse(alert.getTitle()),
						miniMessageManager.parse(alert.getDescription()),
						Title.Times.times(
								Duration.ofSeconds(1),
								Duration.ofSeconds(10),
								Duration.ofSeconds(1)
						)
				)
		);
	}

}
