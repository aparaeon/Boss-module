package gg.mmorealms.module.warps.backend.common.dto;

import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;

@Getter
public class Warp {

	public String name;

	public ServerType serverType;
	public Location location;

	public Warp(String name, ServerType serverType, Location location) {
		this.name = name;
		this.serverType = serverType;
		this.location = location;
	}

}
