package gg.mmorealms.module.voting.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.common.dto.event.user.IsValidUserRequest;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.voting.velocity.VotingConfig;
import gg.mmorealms.module.voting.velocity.VotingVelocityModule;

import java.util.List;

public class VoteManager {

	private @Inject VotingConfig config;
	private @Inject ProxyServer proxy;

	private int votes;

	public synchronized void registerVote(String username) {
		Logger.log("Registering vote for player " + username);

		EngineServer engineServer = VotingVelocityModule.instance().getServerManager().getLowestUsageServer(ServerType.SPAWN);

		if (engineServer == null) {
			Logger.error("Cannot register vote for player " + username + " because we can not find a server available" +
					"for verifying user");
			return;
		}

		Boolean response = new IsValidUserRequest(engineServer.getServerID(), username).sendAndGet();
		if (response == null || !response) {
			Logger.info("Cannot register vote for player " + username + " as this user never joined the server before");
			return;
		}

		votes++;

		List<String> commands = config.voteCommands
				.parse("user", username)
				.parse("current_vote_party", votes)
				.parse("target_vote_party", config.votePartyTarget)
				.parse("votes_left", config.votePartyTarget - votes)
				.parse();

		for (String command : commands) {
			proxy.getCommandManager().executeAsync(
					proxy.getConsoleCommandSource(),
					command
			);
		}

		if (votes == config.votePartyTarget) {
			executeVoteParty();
		}
	}

	private void executeVoteParty() {
		this.votes = 0;
		List<String> commands = config.votePartyCommands.parse();

		for (String command : commands) {
			proxy.getCommandManager().executeAsync(
					proxy.getConsoleCommandSource(),
					command
			);
		}
	}

}
