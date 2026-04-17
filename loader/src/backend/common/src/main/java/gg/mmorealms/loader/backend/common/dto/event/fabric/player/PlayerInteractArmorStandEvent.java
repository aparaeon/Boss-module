package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

@Getter
public class PlayerInteractArmorStandEvent extends LocalRequest<EventResult> {

	private final ServerPlayer player;
	private final Level world;
	private final InteractionHand hand;
	private final Entity entity;
	private final @Nullable EntityHitResult hitResult;

	public PlayerInteractArmorStandEvent(ServerPlayer player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
		super(EventResult.pass());
		this.player = player;
		this.world = world;
		this.hand = hand;
		this.entity = entity;
		this.hitResult = hitResult;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public Location getLocation() {
		return Location.of(entity.getX(), entity.getY(), entity.getZ());
	}
}
