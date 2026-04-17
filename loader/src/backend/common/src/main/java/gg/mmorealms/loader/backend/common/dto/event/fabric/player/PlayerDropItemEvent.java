package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Getter
public class PlayerDropItemEvent extends LocalRequest<Boolean> {

	private final ServerPlayer player;
	private final int slot;
	private final ItemStack stack;

	public PlayerDropItemEvent(ServerPlayer player) {
		super(true);
		this.player = player;
		this.slot = player.getInventory().selected;
		this.stack = player.getInventory().getItem(slot);
	}

	public Location getLocation() {
		return Location.of(player.getX(), player.getY(), player.getZ());
	}
}
