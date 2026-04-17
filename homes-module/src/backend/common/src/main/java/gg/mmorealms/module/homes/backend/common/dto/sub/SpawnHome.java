package gg.mmorealms.module.homes.backend.common.dto.sub;

import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.homes.backend.common.dto.Home;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity(name = "homes_spawn")
@Table(name = "homes_spawn")
@DiscriminatorValue("SPAWN")
@NoArgsConstructor
public class SpawnHome extends Home {
	public SpawnHome(String name, Location location) {
		super(name, location);
	}

	@Override
	public void teleport(User user) {
		Location homeLocation = this.getLocation().clone();

		user.send(IServerLocation.of(ServerType.SPAWN), homeLocation);
	}
}
