package gg.mmorealms.module.realms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.interfaces.IPagedGUI;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.UUID;

public class RealmMembersGUI extends GUI implements IPagedGUI {
	private final static RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	public RealmMembersGUI(User user) {
		super(user, new GUISettings()
			.paged(new GUISettings.PagedSettings()
				.enabled(true)
			)
			.chestSize(6)
		);

		open();
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
		setButton(CONFIG.realmMembersGUI.members);

		IRealm realm = IRealm.getByOwner(user);

		if (realm == null) {
			return;
		}

		List<String> members = realm.getMemberList();

		int entriesIndex = this.getPage() * CONFIG.pagedGUI.slots.size();
		int i = 0;

		while (i < CONFIG.pagedGUI.slots.size() * 2 && entriesIndex + i + 1 < members.size()) {
			String uuid = members.get(entriesIndex + i);
			UUID memberUUID = UUID.fromString(uuid);
			IUser memberUser = IUser.getByUUID(memberUUID);

			setButton(CONFIG.pagedGUI.slots.get(i / 2))
				.name(memberUser.getUsername())
				.display(Items.PLAYER_HEAD)
				.skullOwner(memberUser.getUsername())
				.lore(List.of(members.get(entriesIndex + i + 1)));

			i += 2;
		}
	}
}
