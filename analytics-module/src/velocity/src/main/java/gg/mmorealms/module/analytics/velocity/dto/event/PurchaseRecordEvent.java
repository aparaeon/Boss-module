package gg.mmorealms.module.analytics.velocity.dto.event;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseRecordEvent extends LocalEvent {

	private UserStats userStats;
	private double amount;

}
