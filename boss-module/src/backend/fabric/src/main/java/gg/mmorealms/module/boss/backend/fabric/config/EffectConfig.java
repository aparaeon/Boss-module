package gg.mmorealms.module.boss.backend.fabric.config;

import net.minecraft.core.particles.SimpleParticleType;

import java.util.List;

public class EffectConfig {
	public boolean enabled = false;
	public List<String> particles = List.of();
	public Integer intervalSeconds;
	public int count = 5;
	public double offset = 0.5;

	public transient List<SimpleParticleType> particleOptions;

	/** One-shot burst at spawn (no interval). */
	public static EffectConfig burst(String particleId, int count, double offset) {
		EffectConfig e = new EffectConfig();
		e.enabled = true;
		e.particles = List.of(particleId);
		e.count = count;
		e.offset = offset;
		return e;
	}

	/** Recurring ambient effect at intervalSeconds. */
	public static EffectConfig recurring(String particleId, int intervalSeconds, int count, double offset) {
		EffectConfig e = new EffectConfig();
		e.enabled = true;
		e.particles = List.of(particleId);
		e.intervalSeconds = intervalSeconds;
		e.count = count;
		e.offset = offset;
		return e;
	}
}
