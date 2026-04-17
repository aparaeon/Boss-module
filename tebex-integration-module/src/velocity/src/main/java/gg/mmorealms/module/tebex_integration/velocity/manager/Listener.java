package gg.mmorealms.module.tebex_integration.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.module.analytics.velocity.dto.event.PurchaseRecordEvent;
import gg.mmorealms.module.tebex_integration.velocity.config.TebexIntegrationConfig;
import gg.mmorealms.module.tebex_integration.velocity.dto.CouponData;
import gg.mmorealms.module.tebex_integration.velocity.dto.TebexDiscounts;

public class Listener {

	private @Inject ProxyServer proxy;
	private @Inject TebexIntegrationConfig config;
	private @Inject CouponManager couponManager;

	@EventHandler
	public void onPurchaseRecordEvent(PurchaseRecordEvent event) {
		Player player = proxy.getPlayer(event.getUserStats().getUuid()).orElse(null);

		if (player == null) {
			return;
		}

		for (int i = config.discounts.size() - 1; i >= 0; i--) {
			TebexDiscounts discount = config.discounts.get(i);

			if (event.getAmount() < discount.getThreshold()) {
				continue;
			}

			CouponData coupon = couponManager.generateCoupon(discount.getDiscount(), player.getUsername(), discount.getDuration());
			if (coupon == null) {
				Logger.error("Failed to generate coupon code for player " + player.getUsername() + " with discount " + discount.getDiscount());
			}

			return;
		}
	}

}
