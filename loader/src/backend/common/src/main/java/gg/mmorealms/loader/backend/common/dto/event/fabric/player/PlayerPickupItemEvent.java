package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Getter
public class PlayerPickupItemEvent extends LocalRequest<EventResult> {

	private final Player player;
	private final ItemEntity itemEntity;
	private final ItemStack itemStack;

	public PlayerPickupItemEvent(Player player, ItemEntity itemEntity, ItemStack itemStack) {
		super(EventResult.pass());
		this.player = player;
		this.itemEntity = itemEntity;
		this.itemStack = itemStack;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public Location getLocation() {
		return Location.of(player.getX(), player.getY(), player.getZ());
	}
}
