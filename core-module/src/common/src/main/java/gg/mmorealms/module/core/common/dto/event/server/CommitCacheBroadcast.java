package gg.mmorealms.module.core.common.dto.event.server;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommitCacheBroadcast extends NetworkBroadcast {

	private boolean blocking;

	public CommitCacheBroadcast(boolean blocking) {
		super();
		this.blocking = blocking;
	}

}
