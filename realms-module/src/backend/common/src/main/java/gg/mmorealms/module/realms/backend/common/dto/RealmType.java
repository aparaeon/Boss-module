package gg.mmorealms.module.realms.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import lombok.Getter;
import lombok.ToString;


public enum RealmType {

	CHERRY,
	FOREST,
	PLAINS,
	SAVANNA;

	public RealmType.Properties getProperties() {
		return RealmsBackendModule.instance().getConfig().realmTypeProperties.get(this);
	}

	@Getter
	public static class Properties{
		private final String name;
		private final Location spawnOffset;
		private final String display;
		private final String diskLocation;

		public Properties(String name, Location spawnOffset, String diskLocation, String display) {
			this.name = name;
			this.spawnOffset = spawnOffset;
			this.diskLocation = diskLocation;
			this.display = display;
		}

		public String getRegionLocation(int x, int z){
			return new MessageBuilder("data/realms/{local_path}/r.{x}.{z}.mca")
					.parse("local_path", this.diskLocation)
					.parse("x", x)
					.parse("z", z)
					.parse();
		}
	}

}
