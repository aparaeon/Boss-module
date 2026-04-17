package gg.mmorealms.module.core.backend.fabric.mixin_interfaces;

import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface IPositionTracking {

	@Nullable
	default Location getLastLocation() {
		Vec3 pos = getLastPos();
		if (pos == null) {
			return null;
		}

		return LocationUtils.vecToLocation(pos);
	}

	@Nullable Vec3 getLastPos();

	void setLastPos(Vec3 position);

	default Location getCurrentLocation() {
		Vec3 pos = getCurrentPos();
		return LocationUtils.vecToLocation(pos);
	}

	Vec3 getCurrentPos();

	void setCurrentPos(Vec3 position);

	double getAccumulatedDistance();

	void setAccumulatedDistance(double distance);

	default void addAccumulatedDistance(double distance) {
		setAccumulatedDistance(getAccumulatedDistance() + distance);
	}

	default double getDistance() {
		if (getLastPos() == null) {
			return 0.0;
		}

		return getLastPos().distanceTo(getCurrentPos());
	}

	default double getDistance(Direction.Plane plane) {
		Vec3 last = getLastPos();

		if (last == null) {
			return 0.0;
		}

		Vec3 current = getCurrentPos();

		double dx = current.x - last.x;
		double dy = current.y - last.y;
		double dz = current.z - last.z;

		return switch (plane) {
			case HORIZONTAL -> Math.sqrt(dx * dx + dz * dz);
			case VERTICAL -> Math.abs(dy);
		};
	}

	default double getDistanceHorizontal() {
		return getDistance(Direction.Plane.HORIZONTAL);
	}

	default double getDistanceVertical() {
		return getDistance(Direction.Plane.VERTICAL);
	}
}
