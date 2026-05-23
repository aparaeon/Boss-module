package gg.mmorealms.module.realms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.ConfirmationGUI;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.interfaces.IPagedGUI;
import gg.mmorealms.module.core.common.dto.PlayerList;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import gg.mmorealms.module.realms.common.dto.event.LoadRealmEvent;

import java.util.List;
import java.util.UUID;

public class RealmVisitGUI extends GUI implements IPagedGUI {
	private boolean showPublic;

	private static final RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	public RealmVisitGUI(User user, int page, boolean showPublic) {
		super(user,
			new GUISettings()
				.paged(
					new GUISettings.PagedSettings()
						.enabled(true)
				)
				.chestSize(6)
		);

		this.showPublic = showPublic;
		this.setPage(page);

		open();
	}

	public RealmVisitGUI(User user) {
		this(user, 0, true);
	}

	@Override
	public String getTitleString() {
		return "\uF80f\uF207";
	}

	@Override
	public void draw() {
		setButton(CONFIG.pagedGUI.previous)
			.onClick(this::backPage);
		setButton(CONFIG.pagedGUI.next)
			.onClick(this::nextPage);

		if (showPublic) {
			renderPublic();
		} else {
			renderPrivate();
		}
	}

	private void toggleFilter(ClickType ignored) {
		showPublic = !showPublic;
		setPage(0);
	}

	private void renderPublic() {
		setButton(CONFIG.realmVisitGUI.publicFilter)
			.onClick(this::toggleFilter);

		List<PlayerList.PlayerEntry> list = RealmsBackendModule.instance().getEngineManager().getPlayersList().getList();

		int entriesIndex = this.getPage() * CONFIG.pagedGUI.slots.size();
		int entryIndex = 0;
		int slotIndex = 0;

		while (slotIndex < CONFIG.pagedGUI.slots.size() && entriesIndex + entryIndex < list.size()) {
			PlayerList.PlayerEntry entry = list.get(entriesIndex + entryIndex);
			entryIndex++;

			if (entry.uuid().equals(user.getUUID())) {
				continue;
			}

			setButton(CONFIG.realmVisitGUI.publicRealm, CONFIG.pagedGUI.slots.get(slotIndex))
				.onClick((click) -> teleportToRealm(click, entry.uuid()))
				.placeholder("user", entry.username());

			slotIndex++;
		}
	}

	private void renderPrivate() {
		setButton(CONFIG.realmVisitGUI.privateFilter)
			.onClick(this::toggleFilter);

		List<UUID> membershipList = RealmsUtils.getMembership(user.getUUID());

		int pageOffset = this.getPage() * CONFIG.pagedGUI.slots.size();
		int ownRealmSubtraction = 0;

		for (int index = 0; index < CONFIG.pagedGUI.slots.size() && pageOffset + index < membershipList.size(); index++) {
			UUID ownerUUID = membershipList.get(pageOffset + index);
			IUser ownerUser = IUser.getByUUID(ownerUUID);

			if (ownerUUID.equals(user.getUUID())) {
				ownRealmSubtraction = 1;
				continue;
			}

			setButton(CONFIG.realmVisitGUI.privateRealm, CONFIG.pagedGUI.slots.get(index - ownRealmSubtraction))
				.onClick((click) -> privateInteraction(click, ownerUUID))
				.placeholder("user", ownerUser.getUsername());
		}
	}

	private void privateInteraction(ClickType click, UUID ownerUUID) {
		IUser ownerUser = IUser.getByUUID(ownerUUID);

		switch (click) {
			case MOUSE_LEFT -> teleportToRealm(click, ownerUser);
			case MOUSE_RIGHT -> leaveRealm(click, ownerUser);
		}
	}

	protected void teleportToRealm(ClickType click, UUID ownerUUID) {
		teleportToRealm(click, IUser.getByUUID(ownerUUID));
	}

	protected void teleportToRealm(ClickType click, IUser owner) {
		IRealm realm = IRealm.getByOwner(owner);

		if (realm == null) {
			user.sendMessage(CONFIG.lang.playerHasNoRealm);
			return;
		}

		realm.visit(user.getUUID());
	}

	protected void leaveRealm(ClickType ignored, IUser owner) {
		IRealm realm = IRealm.getByOwner(owner);

		if (realm == null) {
			user.sendMessage(CONFIG.lang.playerHasNoRealm);
			return;
		}

		new ConfirmationGUI(user) {
			@Override
			protected void onConfirm(ClickType click) {
				if (realm.getRootLocation() == null) {
					user.sendMessage(CONFIG.lang.realmOwnerNotOnlineLeaveError);
					new LoadRealmEvent(user.getUUID(), realm.getOwnerUUID()).send();
					close();
					return;
				}

				realm.removeMember(user.getUUID());
				new RealmVisitGUI(user, getPage(), showPublic);
			}

			@Override
			protected void onCancel(ClickType click) {
				new RealmVisitGUI(user, getPage(), showPublic);
			}
		}.open();
	}
}
