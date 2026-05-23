package gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Getter
public abstract class BaseBlockUseEvent extends LocalRequest<EventResult> {

	private final ServerPlayer player;
	private final InteractionHand hand;
	private final BlockPos positon;
	private final Direction face;
	private final ServerLevel world;
	private final ItemStack useItem;
	private final BlockState blockState;

	public BaseBlockUseEvent(ServerPlayer player, InteractionHand hand, BlockPos positon, Direction face, ServerLevel world, BlockState blockState, ItemStack useItem) {
		super(EventResult.pass());
		this.player = player;
		this.hand = hand;
		this.positon = positon;
		this.face = face;
		this.world = world;
		this.blockState = blockState;
		this.useItem = useItem;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public boolean getResultBoolean() {
		return getResult() == EventResult.pass();
	}

	public Vec3 getHitLocation() {
		return new Vec3(positon.getX() + 0.5, positon.getY() + 0.5, positon.getZ() + 0.5)
			.add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
	}

}
