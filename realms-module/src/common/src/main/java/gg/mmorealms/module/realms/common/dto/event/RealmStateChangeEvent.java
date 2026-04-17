package gg.mmorealms.module.realms.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.realms.common.dto.RealmState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class RealmStateChangeEvent extends NetworkEvent {

	private UUID uuid;
	private RealmState state;

	public RealmStateChangeEvent(UUID uuid, RealmState state) {
		this.uuid = uuid;
		this.state = state;
	}
}
