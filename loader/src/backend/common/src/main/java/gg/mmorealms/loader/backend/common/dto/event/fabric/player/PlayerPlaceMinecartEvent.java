package gg.mmorealms.loader.backend.common.dto.event.fabric.player;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.world.item.context.UseOnContext;

@Getter
public class PlayerPlaceMinecartEvent extends LocalRequest<Boolean> {
	private final UseOnContext context;

	public PlayerPlaceMinecartEvent(UseOnContext context) {
		super(true);
		this.context = context;
	}
}
