package gg.mmorealms.loader.backend.common.dto.event.fabric.entity;

import dev.architectury.event.EventResult;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.loader.common.dto.location.Location;
import lombok.Getter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class EntityDamageEvent extends LocalRequest<EventResult> {

	private LivingEntity entity;
	private DamageSource source;
	private float amount;

	public EntityDamageEvent(LivingEntity entity, DamageSource source, float amount) {
		super(EventResult.pass());

		this.entity = entity;
		this.source = source;
		this.amount = amount;
	}

	public void setResult(boolean result) {
		setResult(result ? EventResult.pass() : EventResult.interruptFalse());
	}

	public Location getLocation() {
		return Location.of(entity.getX(), entity.getY(), entity.getZ());
	}
}
