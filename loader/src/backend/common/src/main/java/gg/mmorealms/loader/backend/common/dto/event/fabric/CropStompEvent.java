package gg.mmorealms.loader.backend.common.dto.event.fabric;

import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Getter
public class CropStompEvent extends LocalRequest<Boolean> {
	private final Level level;
	private final Entity entity;

	public CropStompEvent(Level level, Entity entity) {
		super(true);
		this.level = level;
		this.entity = entity;
	}
}
