package gg.mmorealms.module.voting.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.vexsoftware.votifier.velocity.event.VotifierEvent;

public class Listener {

	private @Inject VoteManager voteManager;

	@EventHandler
	public void onVotifierEvent(VotifierEvent event) {
		String username = event.getVote().getUsername();

		voteManager.registerVote(username);
	}

}
