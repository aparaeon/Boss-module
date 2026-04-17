package gg.mmorealms.module.core.common.dto.server_location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExactServerLocation implements IServerLocation {

	private final String class_name = ExactServerLocation.class.getName();
	private String server;

	public static ExactServerLocation of(String server) {
		return new ExactServerLocation(server);
	}

	@Override
	public String toString() {
		return server;
	}
}
