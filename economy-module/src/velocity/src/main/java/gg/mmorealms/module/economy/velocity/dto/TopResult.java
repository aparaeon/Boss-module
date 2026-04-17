package gg.mmorealms.module.economy.velocity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopResult {
	String username;
	double amount;
	String currency;

	public TopResult(String username, double amount, String currency) {
		this.username = username;
		this.amount = amount;
		this.currency = currency;
	}
}
