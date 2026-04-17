package gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user;

import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;


@Getter
public class PlayerBlockPlaceEvent extends BaseBlockUseEvent {

	public PlayerBlockPlaceEvent(ServerPlayer player, InteractionHand hand, BlockPos positon, Direction face, ServerLevel world, BlockState blockState, ItemStack useItem) {
		super(player, hand, positon, face, world, blockState, useItem);
	}

	public Location getLocation() {
		return Location.of(getPositon().getX(), getPositon().getY(), getPositon().getZ());
	}

}