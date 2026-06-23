package gg.mmorealms.module.boss.backend.fabric;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared state between BossChallengeHandlerMixin and non-mixin code.
 * Mixin-package classes cannot be referenced directly from outside the mixin system
 * (IllegalClassLoadError in Mixin 0.16.5+), so the CONFIRMED set lives here instead.
 */
public final class BossMixinState {
	private BossMixinState() {}

	/** Players who clicked Battle in the dialogue GUI; their next challenge bypasses the gate. */
	public static final Set<UUID> CONFIRMED = ConcurrentHashMap.newKeySet();
}
