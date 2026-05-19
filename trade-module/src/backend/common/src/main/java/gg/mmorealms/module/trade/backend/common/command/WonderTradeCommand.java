package gg.mmorealms.module.wondertrade.velocity.command;

import gg.mmorealms.module.wondertrade.velocity.manager.WonderTradeManager;

import java.util.UUID;

public class WonderTradeCommand {

	public void execute(UUID playerId) {

		WonderTradeManager manager = WonderTradeManager.getInstance();

		if (manager.isOnCooldown(playerId)) {

			long remaining = manager.getRemaining(playerId);

			long seconds = remaining / 1000;
			long minutes = seconds / 60;
			seconds = seconds % 60;

			sendMessage(playerId, "You must wait " + minutes + "m " + seconds + "s before trading again.");
			return;
		}

		// TODO: get selected Pokémon from player
		Object selectedPokemon = getSelectedPokemon(playerId);

		if (selectedPokemon == null) {
			sendMessage(playerId, "You must select a Pokémon to trade.");
			return;
		}

		Object received = manager.trade(playerId, selectedPokemon);

		if (received == null) {
			sendMessage(playerId, "Trade failed.");
			return;
		}

		sendMessage(playerId, "Trade complete! You received a Pokémon.");
	}

	// =========================
	// PLACEHOLDERS
	// =========================

	private Object getSelectedPokemon(UUID playerId) {
		// TODO: hook into Cobblemon party
		return new Object();
	}

	private void sendMessage(UUID playerId, String msg) {
		// TODO: hook into your chat system
	}
}