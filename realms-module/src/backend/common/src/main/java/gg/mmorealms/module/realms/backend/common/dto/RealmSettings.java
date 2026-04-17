package gg.mmorealms.module.realms.backend.common.dto;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;

@Getter
@Setter
public class RealmSettings {
	private boolean canAnyoneVisit = false;
	private Long savedDayTime = -1L;
	private boolean rain = false;
	private boolean pokemonSpawning = true;
	private String visitorMessage = "";

	public void invertPokemonSpawning() {
		pokemonSpawning = !pokemonSpawning;
	}

	public void invertCanAnyoneVisit() {
		canAnyoneVisit = !canAnyoneVisit;
	}

	public void removeTime() {
		savedDayTime = -1L;
	}

	public boolean doDaylightCycle() {
		if (savedDayTime == -1) {
			return true;
		}

		return false;
	}

	public void saveTime(IRealm realm) {
		Long dayTime = realm.getDayTime();

		if (dayTime == null) {
			IUser owner = realm.getOwner();
			Logger.error("Could not save time for realm with owner uuid: " + owner.getUUID()
					+ ", username: " + owner.getUsername());
			return;
		}

		savedDayTime = dayTime;
	}

	public void invertWeather() {
		rain = !rain;
	}

	public void apply(ServerPlayer player) {
		if (rain) {
			player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 1.0f));
		} else {
			player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 0.0f));
		}


	}
}
