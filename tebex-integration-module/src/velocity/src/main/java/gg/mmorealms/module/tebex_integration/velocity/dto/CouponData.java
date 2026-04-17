package gg.mmorealms.module.tebex_integration.velocity.dto;

import com.raduvoinea.utils.generic.Time;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.tebex_integration.velocity.TebexIntegrationVelocityModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CouponData {

	private int id;
	private String code;
	private boolean active;

	// For creation
	private String username;
	private double discountPercent;
	private Time expireIn;

	// For creation
	public CouponData(String username, double discountPercent, Time expireIn) {
		this.username = username;
		this.discountPercent = discountPercent;
		this.expireIn = expireIn;
	}

	public void sendMessage() {
		if (this.username == null) {
			return;
		}

		Player player = TebexIntegrationVelocityModule.instance().getProxy().getPlayer(this.username).orElse(null);

		if (player == null) {
			return;
		}

		player.sendMessage(
				TebexIntegrationVelocityModule.instance().getMiniMessageManager().parse(
						TebexIntegrationVelocityModule.instance().getConfig().lang.discountMessage
								.parse("discount", this.discountPercent)
								.parse("code", code)
								.parse("duration", this.expireIn.toString())
								.toString()
				)
		);
	}

}
