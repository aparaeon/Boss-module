package gg.mmorealms.module.tutorial.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.velocity.manager.VelocityPlayerDependentDatabaseLoader;
import gg.mmorealms.module.tutorial.velocity.TutorialVelocityModule;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialProgress;
import gg.mmorealms.module.tutorial.velocity.files.TutorialConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TutorialProgressLoader extends VelocityPlayerDependentDatabaseLoader<TutorialProgress> {

	private final VelocityMiniMessageManager miniMessageManager;
	private final TutorialConfig config;

	public TutorialProgressLoader() {
		super(TutorialProgress.class);
		this.miniMessageManager = TutorialVelocityModule.instance().getMiniMessageManager();
		this.config = TutorialVelocityModule.instance().getConfig();
	}

	@Override
	public void onJoin(@NotNull Player player) {
		TutorialProgress tutorialProgress = TutorialProgress.get(player);

		if(tutorialProgress.isCompleted()) {
			return;
		}

		TutorialVelocityModule.instance().getTutorialManager().addTutorialPlayer(player);
	}

	@Override
	public void onLeave(@NotNull Player player) {
		TutorialVelocityModule.instance().getTutorialManager().removeTutorialPlayer(player);
	}
}
