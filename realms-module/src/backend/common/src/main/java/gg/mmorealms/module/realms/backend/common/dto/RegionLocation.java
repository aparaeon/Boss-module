package gg.mmorealms.module.realms.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class RegionLocation {

	private int x;
	private int z;

	public RegionLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void normalize(int factor) {
		x /= factor;
		x *= factor;

		z /= factor;
		z *= factor;
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public RegionLocation clone() {
		return new RegionLocation(x, z);
	}

	public static RegionLocation convert(Location location) {
		return new RegionLocation((int) (location.getX() / 512), (int) (location.getZ() / 512));
	}

	public Location toLocation() {
		return Location.of(x * 512, 0, z * 512);
	}

	public ChunkLocation toChunkLocation() {
		return new ChunkLocation(x * 32, z * 32);
	}

	@Override
	public String toString() {
		return new MessageBuilder("Region ({x}, {z})")
				.parse("x", x)
				.parse("z", z)
				.parse();
	}
}
