package gg.mmorealms.module.auction_house.backend.common.exception;

public class RateLimitException extends Exception {

	public RateLimitException(String message) {
		super(message);
	}

}
