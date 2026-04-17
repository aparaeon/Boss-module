package gg.mmorealms.module.realms.backend.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.IsRealmCrashedRequest;

public class RealmGUI extends GUI {

	private final static RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	private IRealm realm;

	public RealmGUI(User user) {
		this(user, true);
	}

	public RealmGUI(User user, boolean sendMessages) {
		super(user, new Settings().chestSize(6));

		this.realm = IRealm.getByOwner(this.getUser());

		if (realm == null) {
			if (sendMessages) {
				this.getUser().sendMessage(RealmsBackendModule.instance().getConfig().lang.playerHasNoOwnRealm);
			}
			new RealmCreateGUI(this.getUser()).open();
			return;
		}

		RealmState realmState = realm.getState();

		if (realmState == RealmState.LOADED) {
			open();
			return;
		}

		MessageBuilder message = switch (realmState) {
			case LOADING -> RealmsBackendModule.instance().getConfig().lang.realmStillLoading;
			case LOADED -> null;
			case CRASHED -> RealmsBackendModule.instance().getConfig().lang.realmOnCrashingServer;
			case UNLOADING -> RealmsBackendModule.instance().getConfig().lang.realmUnloading;
			case null ->
					new MessageBuilder("<red>Something went wrong while trying to load your realm, please relog."); // TODO Config
		};

		if (message == null) {
			return;
		}

		user.sendMessage(message
				.parse("pre", "Your")
		);
	}

	@Override
	public String getTitleString() {
		return "\uF80F\uF204";
	}

	@Override
	public void setup() {
		setButton(CONFIG.realmGUI.teleport)
				.onClick(this::teleportToRealm);
		setButton(CONFIG.realmGUI.members)
				.onClick(this::showMembers);
		setButton(CONFIG.realmGUI.settings)
				.onClick(this::showSettings);
	}

	public void teleportToRealm(ClickType action) {
		realm.send(this.getUser());
	}

	public void showMembers(ClickType action) {
		new RealmMembersGUI(this.getUser());
	}

	public void showSettings(ClickType action) {
		new RealmSettingsGUI(this.getUser());
	}
}
