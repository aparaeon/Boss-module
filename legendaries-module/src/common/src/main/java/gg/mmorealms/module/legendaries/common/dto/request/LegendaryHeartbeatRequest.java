package gg.mmorealms.module.legendaries.common.dto.request;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class LegendaryHeartbeatRequest extends NetworkRequest<LegendaryHeartbeatRequest.Response> {
	private LegendaryHeartbeatRequest(@NotNull String targetServerId) {
		super(targetServerId);
	}

	public static LegendaryHeartbeatRequest to(@NotNull String targetServerId) {
		return new LegendaryHeartbeatRequest(targetServerId);
	}

	@Getter
	@AllArgsConstructor
	public static class Response implements Serializable {
		private List<LegendaryInfo> info;
	}
}
