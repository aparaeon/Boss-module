package gg.mmorealms.module.homes.backend.common.dto.sub;

import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.dto.Home;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity(name = "homes_wild")
@Table(name = "homes_wild")
@DiscriminatorValue("WILD")
@NoArgsConstructor
public class WildHome extends Home {
	protected String serverID;

	public WildHome(String name, Location location) {
		super(name, location);

		this.serverID = BackendLoader.instance().getServerID();
	}

	@Override
	public void teleport(User user) {
		HomesConfig config = HomesBackendModule.instance().getConfig();

		Location homeLocation = this.getLocation().clone();

		if (config.lang.warningTeleportingToWildHome != null) {
			user.sendMessage(config.lang.warningTeleportingToWildHome);
		}

		user.send(IServerLocation.of(serverID), homeLocation);
	}
}
