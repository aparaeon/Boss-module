package gg.mmorealms.module.gyms.backend.common.dto.trainer;

import gg.mmorealms.loader.common.dto.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TrainerInfo {
	private String id;
	private Location location;
	private int level;

	public TrainerInfo(Location location) {
		this.location = location;
	}
}