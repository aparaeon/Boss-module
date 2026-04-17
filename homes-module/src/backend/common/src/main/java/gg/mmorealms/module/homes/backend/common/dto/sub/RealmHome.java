package gg.mmorealms.module.homes.backend.common.dto.sub;

import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.dto.Home;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "homes_realm")
@Table(name = "homes_realm")
@DiscriminatorValue("REALM")
@NoArgsConstructor
public class RealmHome extends Home {
	private UUID realmRootUserUUID;

	private RealmHome(String name, Location location, IRealm realm) {
		super(name, location);

		this.realmRootUserUUID = realm.getOwnerUUID();
	}

	public static RealmHome create(String name, Location location, IRealm realm) {
		location = location.offset(realm.getRootLocation().toLocation().multiply(-1));
		return new RealmHome(name, location, realm);
	}

	@Override
	public void teleport(User user) {
		HomesConfig config = HomesBackendModule.instance().getConfig();

		Location homeLocation = this.getLocation().clone();
		IUser realmUser = IUser.getByUUID(realmRootUserUUID);
		if (!realmUser.isOnlineOnNetwork()) {
			user.sendMessage(config.lang.teleportToOfflinePlayerRealmHome);
			return;
		}

		IRealm realm = IRealm.getByOwner(realmRootUserUUID);
		if (realm == null) {
			user.sendMessage(config.lang.errorRealmNotFound);
			return;
		}
		RegionLocation realmRootRegionLocation = realm.getRootLocation();
		if (realmRootRegionLocation == null) {
			user.sendMessage(config.lang.errorRealmNotLoaded);
			return;
		}

		Location realmRootLocation = realmRootRegionLocation.toLocation();
		homeLocation = homeLocation.offset(realmRootLocation);

		user.send(IServerLocation.of(realm.getServerID()), homeLocation);
	}
}
