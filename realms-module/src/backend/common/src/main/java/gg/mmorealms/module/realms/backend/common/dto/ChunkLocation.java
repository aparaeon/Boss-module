package gg.mmorealms.module.realms.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ChunkLocation {

	private int x;
	private int z;

	public ChunkLocation(int x, int z) {
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
	public ChunkLocation clone() {
		return new ChunkLocation(x, z);
	}

	public static ChunkLocation convert(Location location) {
		return new ChunkLocation((int) (location.getX() / 16), (int) (location.getZ() / 16));
	}

	public Location toLocation() {
		return Location.of(x * 16, 0, z * 16);
	}

	public ChunkLocation offsetNew(ChunkLocation offset) {
		return new ChunkLocation(x + offset.x, z + offset.z);
	}

	public ChunkLocation offsetNegativeNew(ChunkLocation offset) {
		return new ChunkLocation(x - offset.x, z - offset.z);
	}

	@Override
	public String toString() {
		return new MessageBuilder("Chunk ({x}, {z})")
				.parse("x", x)
				.parse("z", z)
				.parse();
	}
}
