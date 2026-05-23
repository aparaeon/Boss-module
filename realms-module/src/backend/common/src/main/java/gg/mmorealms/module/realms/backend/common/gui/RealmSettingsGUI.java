package gg.mmorealms.module.realms.backend.common.gui;


import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmSettings;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;

import java.util.List;

public class RealmSettingsGUI extends GUI {
	private final static RealmsConfig config = RealmsBackendModule.instance().getConfig();

	public RealmSettingsGUI(User user) {
		super(user, new GUISettings().chestSize(6));

		open();
	}

	@Override
	public String getTitleString() {
		return "\uF80F\uF206";
	}

	@Override
	public void draw() {
		IRealm realm = IRealm.getByOwner(user);

		if (realm == null) {
			user.sendMessage(config.lang.howDoYouGetInRealmSettingsWithoutARealm);
			this.close();
			return;
		}

		renderPrivacy(realm, realm.getSettings().isCanAnyoneVisit());
		renderTime(realm, realm.getSettings().doDaylightCycle());
		renderWeather(realm, realm.getSettings().isRain());
		renderPokemonSpawning(realm, realm.getSettings().isPokemonSpawning());
	}

	private void renderPrivacy(IRealm realm, boolean enabled) {
		List<GUIButton> buttons = enabled
				? List.of(config.realmSettingsGUI.publicLeftButton,
				config.realmSettingsGUI.publicRightButton)
				: List.of(config.realmSettingsGUI.privateLeftButton,
				config.realmSettingsGUI.privateRightButton);


		for (GUIButton button : buttons) {
			setButton(button).onClick((click) -> changeVisit(click, realm));
		}
	}

	private void renderPokemonSpawning(IRealm realm, boolean enabled) {
		List<GUIButton> buttons = enabled
				? List.of(
				config.realmSettingsGUI.onPokemonSpawningLeftButton,
				config.realmSettingsGUI.onPokemonSpawningRightButton)
				: List.of(config.realmSettingsGUI.offPokemonSpawningButton);

		for (GUIButton button : buttons) {
			setButton(button).onClick((click) -> changePokemonSpawning(click, realm));
		}
	}

	private void renderWeather(IRealm realm, boolean enabled) {
		List<GUIButton> buttons = enabled
				? List.of(
				config.realmSettingsGUI.onWeatherLeftButton,
				config.realmSettingsGUI.onWeatherRightButton)
				: List.of(config.realmSettingsGUI.offWeatherButton);

		for (GUIButton button : buttons) {
			setButton(button).onClick((click) -> changeWeather(click, realm));
		}
	}

	private void renderTime(IRealm realm, boolean enabled) {
		List<GUIButton> buttons = enabled
				? List.of(
				config.realmSettingsGUI.onTimeLeftButton,
				config.realmSettingsGUI.onTimeRightButton)
				: List.of(config.realmSettingsGUI.offTimeButton);

		for (GUIButton button : buttons) {
			setButton(button).onClick((click) -> changeTime(click, realm));
		}
	}

	private void changeVisit(ClickType click, IRealm realm) {
		RealmSettings settings = realm.getSettings();
		settings.invertCanAnyoneVisit();
		realm.setSettings(settings);

		refresh();
	}

	private void changeWeather(ClickType click, IRealm realm) {
		if (!user.hasPermission(config.permission
				.parse("permission", "weather")
				.parse())) {
			user.sendMessage(config.realmSettingsGUI.noPermissionWeather);
			return;
		}

		RealmSettings settings = realm.getSettings();
		settings.invertWeather();
		realm.setSettings(settings);

		refresh();
	}

	private void changeTime(ClickType click, IRealm realm) {
		if (!user.hasPermission(config.permission
				.parse("permission", "time")
				.parse())) {
			user.sendMessage(config.realmSettingsGUI.noPermissionTime);
			return;
		}

		RealmSettings settings = realm.getSettings();

		if (settings.doDaylightCycle()) {
			settings.saveTime(realm);
		} else {
			settings.removeTime();
		}


		realm.setSettings(settings);
		refresh();
	}

	private void changePokemonSpawning(ClickType click, IRealm realm) {
		if (!user.hasPermission(config.permission
				.parse("permission", "pokemon_spawning")
				.parse())) {
			user.sendMessage(config.realmSettingsGUI.noPermissionPokemonSpawn);
			return;
		}

		RealmSettings settings = realm.getSettings();

		settings.invertPokemonSpawning();

		realm.setSettings(settings);
		refresh();
	}
}
