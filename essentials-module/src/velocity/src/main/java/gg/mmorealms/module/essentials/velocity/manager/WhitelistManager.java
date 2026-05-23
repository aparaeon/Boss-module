package gg.mmorealms.module.essentials.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.config.EssentialsConfig;
import gg.mmorealms.module.essentials.velocity.dto.WhitelistState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;

@Getter
public class WhitelistManager {

	private WhitelistState state;

	public WhitelistManager(EssentialsConfig config) {
		this.state = config.defaultWhitelistState;

		this.state.setBypassPlayers(new HashSet<>(this.state.getBypassPlayers()));
		this.state.setWhitelistedServers(new ArrayList<>(this.state.getWhitelistedServers()));
		this.state.setWhitelistedServerTypes(new ArrayList<>(this.state.getWhitelistedServerTypes()));

		for (String whitelistedServer : this.state.getWhitelistedServers()) {
			EssentialsVelocityModule.instance().getServerManager().disableServer(whitelistedServer);
		}

		for (ServerType whitelistedServerType : this.state.getWhitelistedServerTypes()) {
			EssentialsVelocityModule.instance().getServerManager().disableServerType(whitelistedServerType);
		}
	}

	public void setBypass(String name, boolean status) {
		if (status) {
			this.state.getBypassPlayers().add(name);
		} else {
			this.state.getBypassPlayers().remove(name);
		}
	}

	public void setWhitelist(String serverID, boolean status) {
		if (status) {
			this.state.getWhitelistedServers().add(serverID);
			EssentialsVelocityModule.instance().getServerManager().disableServer(serverID);
		} else {
			this.state.getWhitelistedServers().remove(serverID);
			EssentialsVelocityModule.instance().getServerManager().enableServer(serverID);
		}
	}

	public void setWhitelist(ServerType serverType, boolean status) {
		if (status) {
			this.state.getWhitelistedServerTypes().add(serverType);
			EssentialsVelocityModule.instance().getServerManager().disableServerType(serverType);
		} else {
			this.state.getWhitelistedServerTypes().remove(serverType);
			EssentialsVelocityModule.instance().getServerManager().enableServerType(serverType);
		}
	}

	public void setWhitelist(boolean status) {
		this.state.setGlobalEnabled(status);
	}

	public boolean isAllowed(RegisteredServer server, Player player) {
		if(this.state.isGlobalEnabled()) {
			return isBypassPlayer(player);
		}

		if(this.state.getWhitelistedServers().isEmpty() && this.state.getWhitelistedServerTypes().isEmpty()) {
			return true;
		}

		if (this.state.getWhitelistedServers().contains(server.getServerInfo().getName())) {
			return isBypassPlayer(player);
		}

		EngineServer engineServer = EssentialsVelocityModule.instance().getServerManager().getServer(server.getServerInfo());

		if (engineServer == null) {
			return false;
		}

		if (this.state.getWhitelistedServerTypes().contains(engineServer.getType())) {
			return isBypassPlayer(player);
		}

		return true;
	}

	private boolean isBypassPlayer(Player player){
		return this.state.getBypassPlayers().contains(player.getUsername());
	}


}
