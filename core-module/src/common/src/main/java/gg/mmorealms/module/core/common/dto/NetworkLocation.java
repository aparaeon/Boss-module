package gg.mmorealms.module.core.common.dto;

import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetworkLocation {
	private String server;
	private Location location;

	public NetworkLocation(String server, Location location) {
		this.server = server;
		this.location = location;
	}
}
