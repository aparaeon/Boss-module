package gg.mmorealms.module.wondertrade.velocity.manager;

import java.util.*;

public class WonderTradeManager {

	private static final WonderTradeManager INSTANCE = new WonderTradeManager();

	private final List<Object> pool = new ArrayList<>();
	private final Map<UUID, Long> cooldowns = new HashMap<>();

	private static final long COOLDOWN = 60 * 60 * 1000; // 1 hour

	private WonderTradeManager() {}

	public static WonderTradeManager getInstance() {
		return INSTANCE;
	}

	// =========================
	// COOLDOWN
	// =========================

	public boolean isOnCooldown(UUID playerId) {
		Long last = cooldowns.get(playerId);

		if (last == null) return false;

		return System.currentTimeMillis() - last < COOLDOWN;
	}

	public long getRemaining(UUID playerId) {
		Long last = cooldowns.get(playerId);

		if (last == null) return 0;

		long remaining = COOLDOWN - (System.currentTimeMillis() - last);
		return Math.max(0, remaining);
	}

	private void applyCooldown(UUID playerId) {
		cooldowns.put(playerId, System.currentTimeMillis());
	}

	// =========================
	// TRADE LOGIC
	// =========================

	public Object trade(UUID playerId, Object submittedPokemon) {

		// TODO: Replace Object with actual Pokemon class

		if (submittedPokemon == null) {
			return null;
		}

		// TODO: check isEgg()

		if (isOnCooldown(playerId)) {
			return null;
		}

		Object received;

		if (pool.isEmpty()) {
			received = generateFallback();
		} else {
			int index = new Random().nextInt(pool.size());
			received = pool.remove(index);
		}

		pool.add(submittedPokemon);

		applyCooldown(playerId);

		// TODO: announce shiny / legendary

		return received;
	}

	// =========================
	// FALLBACK
	// =========================

	private Object generateFallback() {
		// TODO: generate real Pokemon
		return new Object();
	}

	// =========================
	// ADMIN
	// =========================

	public int getPoolSize() {
		return pool.size();
	}

	public void clearPool() {
		pool.clear();
	}
}
