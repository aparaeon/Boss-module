package gg.mmorealms.module.homes.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.PagedGUI;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.dto.Home;
import gg.mmorealms.module.homes.backend.common.dto.IHomes;
import gg.mmorealms.module.homes.backend.common.utils.HomesUtils;

import java.util.List;

public class HomesGUI extends PagedGUI {
	private final static HomesConfig CONFIG = HomesBackendModule.instance().getConfig();
	private final static List<Integer> slots = CONFIG.homesGUI.slots;
	private final int countAvailableHomes;
	private final IHomes homes;

	public HomesGUI(User user) {
		super(user, new Settings().chestSize(6));
		countAvailableHomes = HomesUtils.getMaxHomes(this.getUser().getPlayer());
		homes = IHomes.getByUser(this.getUser());
	}

	@Override
	public String getTitleString() {
		return CONFIG.lang.titleHomesGUI
				.parse("page", getPage() + 1)
				.toString();
	}

	@Override
	public void setup() {
		CONFIG.homesGUI.background.forEach(this::setButton);
		setButton(CONFIG.homesGUI.addHome).onClick(this::addHomeClick);
		setButton(CONFIG.homesGUI.previousPageItem).onClick(this::previousPage);
		setButton(CONFIG.homesGUI.nextPageItem).onClick(this::nextPage);

		int startIndex = slots.size() * getPage();
		int slotIndex;
		for (slotIndex = 0; slotIndex + startIndex < homes.size() && slotIndex < slots.size(); slotIndex++) {
			Home home = homes.get(slotIndex + startIndex);

			setButton(CONFIG.homesGUI.assignedHome, slots.get(slotIndex))
					.placeholder("home_name", home.getName())
					.onClick((click) -> homeClick(home, click));
		}

		int maxHomes = countAvailableHomes - slotIndex - startIndex;
		int endOfAvailableHomes = Math.min(slots.size(), slotIndex + maxHomes);

		setButton(CONFIG.homesGUI.availableHome
				.position(slots.subList(slotIndex, endOfAvailableHomes))
		);
		setButton(CONFIG.homesGUI.unavailableHome
				.position(slots.subList(endOfAvailableHomes, slots.size()))
		);
	}

	@Override
	protected int getPagesCount() {
		return countAvailableHomes / (slots.size() + 1) + 1;
	}

	private void addHomeClick(ClickType click) {
		this.close();
		this.getUser().sendMessage(CONFIG.lang.messageAddHomeHomesGUI);
	}

	private void homeClick(Home home, ClickType click) {
		if (click == ClickType.MOUSE_LEFT) {
			teleportToHome(home);
		} else if (click == ClickType.MOUSE_RIGHT) {
			deleteHome(home);
		}
	}

	private void teleportToHome(Home home) {
		home.teleport(this.getUser());
	}

	private void deleteHome(Home home) {
		IHomes homes = IHomes.getByUser(this.getUser());
		homes.remove(home.getName());
		this.refresh();
	}
}
