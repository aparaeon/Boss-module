package gg.mmorealms.loader.backend.common.dto.event.fabric.entity;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@Getter
public class MinecartTickEvent extends LocalRequest<Boolean> {
	private final AbstractMinecart minecart;

	public MinecartTickEvent(AbstractMinecart minecart) {
		super(true);
		this.minecart = minecart;
	}
}
