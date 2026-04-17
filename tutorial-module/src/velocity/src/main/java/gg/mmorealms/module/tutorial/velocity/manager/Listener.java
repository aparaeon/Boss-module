package gg.mmorealms.module.tutorial.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;
import gg.mmorealms.module.pokemon.common.dto.PlayerChoseStarterS2PEvent;
import gg.mmorealms.module.pokemon.common.dto.PlayerSendPokemonS2PEvent;
import gg.mmorealms.module.tutorial.velocity.TutorialVelocityModule;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialProgress;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialStep;

public class Listener {

	private @Inject ProxyServer proxy;

	@EventHandler
	public void onCommandExecuteEvent(CommandExecuteEvent event) {
		String command = event.getCommand().strip().toLowerCase();

		if (!(event.getCommandSource() instanceof Player player)) {
			return;
		}

		TutorialProgress tutorialProgress = TutorialProgress.get(player);

		if (tutorialProgress.isCompleted()) {
			return;
		}

		switch (command) {
			case "rtp", "wild" -> tutorialProgress.attemptToFinish(TutorialStep.RTP);
			case "warp heal" -> tutorialProgress.attemptToFinish(TutorialStep.WARP_HEAL);
			case "realm teleport", "realm tp", "realm" -> tutorialProgress.attemptToFinish(TutorialStep.REALM_TP);
		}
	}

	@EventHandler
	public void onLocalChatRequest(LocalChatRequest event) {
		for (Player tutorialPlayer : TutorialVelocityModule.instance().getTutorialManager().getTutorialPlayers()) {
			event.getRecipients().remove(tutorialPlayer.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerChoseStarterEvent(PlayerChoseStarterS2PEvent event) {
		Player player = proxy.getPlayer(event.getUuid()).orElse(null);

		if (player == null) {
			return;
		}

		TutorialProgress tutorialProgress = TutorialProgress.get(player);
		tutorialProgress.attemptToFinish(TutorialStep.CLAIM_STARTER);
	}

	@EventHandler
	public void onPlayerChoseStarterEvent(PlayerSendPokemonS2PEvent event) {
		Player player = proxy.getPlayer(event.getUuid()).orElse(null);

		if (player == null) {
			return;
		}

		TutorialProgress tutorialProgress = TutorialProgress.get(player);
		tutorialProgress.attemptToFinish(TutorialStep.SEND_POKEMON);
	}

}
