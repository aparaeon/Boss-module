package gg.mmorealms.module.analytics.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
public class UserSpentAmountsRequest extends NetworkRequest<UserSpentAmountsRequest.Response> {

	private UUID uuid;

	public UserSpentAmountsRequest(UUID uuid) {
		super();
		this.uuid = uuid;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Response {
		private double totalSpentLastMonth;
		private double totalSpent;
	}

}
