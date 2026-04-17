package gg.mmorealms.module.realms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.PagedGUI;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmType;

public class RealmCreateGUI extends PagedGUI {

	private final static RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	public RealmCreateGUI(User user) {
		this(user, 0);
	}

	public RealmCreateGUI(User user, int typeIndex) {
		super(user, new Settings()
				.wrapPage(true)
				.chestSize(6), typeIndex);

		open();
	}

	@Override
	public String getTitleString() {
		return CONFIG.realmCreateGUI.titleBase + getRealmType(this.getPage()).getProperties().getDisplay();
	}

	public RealmType getRealmType() {
		return getRealmType(this.getPage());
	}

	private static RealmType getRealmType(int index) {
		return RealmType.values()[index];
	}

	@Override
	protected int getPagesCount() {
		return RealmType.values().length;
	}

	@Override
	public void setup() {
		setButton(CONFIG.realmCreateGUI.previous)
				.onClick(this::previousPage);
		setButton(CONFIG.realmCreateGUI.next)
				.onClick(this::nextPage);
		setButton(CONFIG.realmCreateGUI.create)
				.onClick(this::createRealm);
	}

	private void createRealm(ClickType action) {
		RealmsBackendModule.instance().getRealmsManager().createRealm(getUser(), getRealmType());
		close();
	}
}
