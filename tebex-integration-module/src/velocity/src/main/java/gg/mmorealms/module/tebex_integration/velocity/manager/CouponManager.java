package gg.mmorealms.module.tebex_integration.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.tebex_integration.velocity.TebexIntegrationVelocityModule;
import gg.mmorealms.module.tebex_integration.velocity.dto.CouponData;
import gg.mmorealms.module.tebex_integration.velocity.utils.TebexUtils;

import java.util.ArrayList;
import java.util.List;

public class CouponManager {

	private final List<Integer> activeCoupons = new ArrayList<>();
	private final CancelableTimeTask reminderTask;

	public CouponManager() {
		this.reminderTask = ScheduleUtils.runTaskTimer(
				this::remind,
				TebexIntegrationVelocityModule.instance().getConfig().reminderTimer
		);
	}

	public CouponData generateCoupon(double discountPercent, String username, Time expireIn) {
		CouponData coupon = null;
		int retries = 0;

		while (coupon == null && retries < 10) {
			coupon = TebexUtils.createCoupon(new CouponData(username, discountPercent, expireIn));
			retries++;
		}

		if (coupon == null) {
			return null;
		}

		activeCoupons.add(coupon.getId());
		return coupon;
	}

	private void remind() {
		Logger.debug("Reminding about active coupons...");
		List<Integer> toRemove = new ArrayList<>();

		for (Integer activeCouponID : activeCoupons) {
			Logger.debug("Active coupon ID: " + activeCouponID);
			CouponData coupon = TebexUtils.getCouponData(activeCouponID);

			if (coupon == null) {
				Logger.debug("Invalid coupon.");
				toRemove.add(activeCouponID);
				continue;
			}

			if (!coupon.isActive()) {
				Logger.debug("Not active");
				toRemove.add(activeCouponID);
				continue;
			}

			Logger.debug("Sending message");
			coupon.sendMessage();
		}

		activeCoupons.removeAll(toRemove);
	}
}
