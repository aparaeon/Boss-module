package gg.mmorealms.module.realms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.ConfirmationGUI;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.ChunkLocation;
import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;

public class ChunkUnlockGUI extends ConfirmationGUI {

	private final static RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	private final Realm realm;
	private final ChunkLocation targetChunkLocation;

	public ChunkUnlockGUI(User user, Realm realm, ChunkLocation chunkLocation) {
		super(user);
		this.realm = realm;
		this.targetChunkLocation = chunkLocation;
		this.checkBalance();
	}

	@Override
	protected void onConfirm(ClickType click) {
		if (!checkBalance()) {
			return;
		}

		IBalances balances = IBalances.getByUser(user);
		balances.remove(CONFIG.realmExpansionPrice, "REALM_EXPANSION");

		realm.unlockChunk(targetChunkLocation);
		this.close();
	}

	private boolean checkBalance() {
		IBalances balances = IBalances.getByUser(user);
		if (!balances.has(CONFIG.realmExpansionPrice)) {
			user.sendMessage("You do not have enough money to unlock this chunk."); // TODO Config
			close();
			return false;
		}
		return true;
	}

}
