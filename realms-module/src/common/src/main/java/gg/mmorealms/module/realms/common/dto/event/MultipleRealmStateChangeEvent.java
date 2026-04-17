package gg.mmorealms.module.realms.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.realms.common.dto.RealmState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class MultipleRealmStateChangeEvent extends NetworkEvent {

	private RealmState state;

	public MultipleRealmStateChangeEvent( RealmState state) {
		this.state = state;
	}
}
