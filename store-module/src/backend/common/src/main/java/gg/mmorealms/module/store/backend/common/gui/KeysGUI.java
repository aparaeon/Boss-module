package gg.mmorealms.module.store.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import gg.mmorealms.module.store.backend.common.dto.StoreKey;
import gg.mmorealms.module.store.backend.common.files.StoreConfig;

import java.util.List;

public class KeysGUI extends GenericStoreGUI<StoreKey> {

	private static final StoreConfig CONFIG = StoreBackendModule.instance().getConfig();

	public KeysGUI(User user, IUser target) {
		super(user, target, "\uF220", "\uF21F");
	}

	@Override
	protected int getDiscount() {
		return 0;
	}

	@Override
	protected List<StoreKey> getAllEntries() {
		return CONFIG.keys;
	}
}
