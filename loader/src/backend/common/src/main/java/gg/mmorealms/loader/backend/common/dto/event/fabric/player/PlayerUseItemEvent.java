package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import dev.architectury.event.CompoundEventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Getter
public class PlayerUseItemEvent extends LocalRequest<CompoundEventResult<ItemStack>> {

	private final ServerPlayer player;
	private final InteractionHand hand;

	public PlayerUseItemEvent(ServerPlayer player, InteractionHand hand) {
		super(CompoundEventResult.pass());
		this.player = player;
		this.hand = hand;
	}

	public void setResult(boolean result) {
		setResult(result ? CompoundEventResult.pass() : CompoundEventResult.interruptFalse(player.getItemInHand(hand)));
	}

	public Location getLocation() {
		return Location.of(player.getX(), player.getY(), player.getZ());
	}
}
