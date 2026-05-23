package gg.mmorealms.module.realms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.interfaces.IPagedGUI;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmType;

public class RealmCreateGUI extends GUI implements IPagedGUI {

	private final static RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	public RealmCreateGUI(User user) {
		this(user, 0);
	}

	public RealmCreateGUI(User user, int typeIndex) {
		super(user, new GUISettings()
			.paged(new GUISettings.PagedSettings()
				.enabled(true)
				.wrap(true)
			)
			.chestSize(6));

		this.setPage(typeIndex);

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
	public int getPagesCount() {
		return RealmType.values().length;
	}

	@Override
	public void draw() {
		setButton(CONFIG.realmCreateGUI.previous)
			.onClick(this::backPage);
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
