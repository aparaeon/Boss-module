package gg.mmorealms.loader.common.dto.location;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import lombok.Setter;

import java.util.UUID;

public class UUIDLocation implements ILocation {

	@Setter
	public static ReturnArgLambda<Location, UUID> LOCATION_FETCH = (uuid) -> {
		throw new UnsupportedOperationException("UUIDLocation was not registered");
	};

	private final UUID uuid;

	public UUIDLocation(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public Location toLocation() {
		return LOCATION_FETCH.run(uuid);
	}
}
