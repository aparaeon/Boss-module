package gg.mmorealms.module.core.backend.fabric.mixin;

import gg.mmorealms.module.core.backend.fabric.mixin_interfaces.IPositionTracking;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Getter
@Setter
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IPositionTracking {

	@Unique
	private final ServerPlayer self = (ServerPlayer) (Object) this;

	@Unique
	private double accumulatedDistance = 0.0;

	@Unique
	private Vec3 lastPos = null;

	@Override
	public Vec3 getCurrentPos() {
		return self.position();
	}

	@Override
	public void setCurrentPos(Vec3 position) {
	}

}
