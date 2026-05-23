package gg.mmorealms.loader.common.dto.location;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class Location implements ILocation {

	protected @Nullable String world;

	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;

	private static LocationBuilder builder() {
		return new LocationBuilder();
	}

	public static Location of(double x, double y, double z) {
		return builder(x, y, z).build();
	}

	public static LocationBuilder builder(double x, double y, double z) {
		return builder()
			.x(x)
			.y(y)
			.z(z);
	}

	public static Location min(Location... locations) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;

		for (Location location : locations) {
			minX = Double.min(minX, location.getX());
			minY = Double.min(minY, location.getY());
			minZ = Double.min(minZ, location.getZ());
		}

		return Location.builder(minX, minY, minZ).build();
	}

	public static Location max(Location... locations) {
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;

		for (Location location : locations) {
			maxX = Double.max(maxX, location.getX());
			maxY = Double.max(maxY, location.getY());
			maxZ = Double.max(maxZ, location.getZ());
		}

		return Location.builder(maxX, maxY, maxZ).build();
	}

	public Location offset(double x, double y, double z) {
		return offset(x, y, z, 0, 0);
	}

	public Location offset(double x, double y, double z, float pitch, float yaw) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.pitch += pitch;
		this.yaw += yaw;

		return this;
	}

	public Location offset(Location location) {
		return offset(location.x, location.y, location.z, location.pitch, location.yaw);
	}

	public Location offsetNew(double x, double y, double z) {
		return offsetNew(x, y, z, 0, 0);
	}

	public Location offsetNew(double x, double y, double z, float pitch, float yaw) {
		return new Location(
			world,
			this.x + x,
			this.y + y,
			this.z + z,
			this.pitch + pitch,
			this.yaw + yaw
		);
	}

	public Location offsetNew(Location location) {
		return offsetNew(
			location.x,
			location.y,
			location.z,
			location.pitch,
			location.yaw
		);
	}

	public Location multiply(double factor) {
		this.x *= factor;
		this.y *= factor;
		this.z *= factor;

		return this;
	}

	public Location multiplyNew(double factor) {
		Location output = clone();

		output.x *= factor;
		output.y *= factor;
		output.z *= factor;

		return output;
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public Location clone() {
		return new Location(world, x, y, z, pitch, yaw);
	}

	public boolean equalsCoords(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Location location)) {
			return false;
		}

		return x == location.x && y == location.y && z == location.z;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + pitch + ", " + yaw + ")";
	}

	@Override
	public Location toLocation() {
		return this;
	}

	public double distance(Location location) {
		double xDiff = location.x - x;
		double yDiff = location.y - y;
		double zDiff = location.z - z;
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
	}

	public double distance2D(Location location) {
		double xDiff = location.x - x;
		double zDiff = location.z - z;
		return Math.sqrt(xDiff * xDiff + zDiff * zDiff);
	}
}
