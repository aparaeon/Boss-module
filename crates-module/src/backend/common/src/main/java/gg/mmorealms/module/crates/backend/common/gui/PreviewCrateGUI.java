package gg.mmorealms.module.crates.backend.common.gui;


import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.exceptions.PermissionException;
import gg.mmorealms.module.core.backend.common.gui.ConfirmationGUI;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.database.ICrateKeys;
import gg.mmorealms.module.crates.backend.common.dto.Crate;
import gg.mmorealms.module.crates.backend.common.dto.CrateItem;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;

public class PreviewCrateGUI extends GUI {

	private static final MessageBuilder PREVIEW_CRATE_PERMISSION = new MessageBuilder("mmorealms.crate.preview.{id}");
	private static final CratesConfig BASE_CONFIG = CratesBackendModule.instance().getConfig();
	private static final CratesConfig.CratePreviewGUI GUI_CONFIG = BASE_CONFIG.cratePreviewGUI;

	private final Crate crate;

	public PreviewCrateGUI(User user, Crate crate) throws PermissionException {
		super(user, GUI_CONFIG.settings);

		if (!user.hasPermission(PREVIEW_CRATE_PERMISSION
				.parse("id", crate.id)
				.parse())) {
			throw new PermissionException("You do not have permission to preview this crate.");
		}

		this.crate = crate;
		this.crate.bake();
	}

	@Override
	public String getTitleString() {
		return GUI_CONFIG.title
				.parse("name", crate.name)
				.parse();
	}

	@Override
	public void setup() {
		setButton(GUI_CONFIG.background);

		ICrateKeys crateKeys = ICrateKeys.getByUser(this.user);
		int keys = crateKeys.getKeys(crate.getId());

		setButton(GUI_CONFIG.closeItem)
				.onClick(this::openMainMenu);

		setButton(GUI_CONFIG.openItem
				.onClick(this::attemptPurchaseCrate)
				.placeholder("keys", keys)
				.placeholder("price", BASE_CONFIG.getCratePriceMessage(keys, crate.price)
						.parse("price", NumberUtils.formatNumberWithCommas(crate.getPrice().amount()))
						.parse("keys", keys)
				)
		);

		for (int index = 0; index < Math.min(GUI_CONFIG.lootPositions.size(), crate.crateItems.size()); index++) {
			CrateItem crateItem = crate.crateItems.get(index);
			GUIButton displayItem = crateItem.getDisplayItem();

			setButton(displayItem, GUI_CONFIG.lootPositions.get(index));
		}
	}

	public void attemptPurchaseCrate(ClickType clickType) {
		new ConfirmationGUI(user) {
			@Override
			protected void onConfirm(ClickType click) {
				purchaseCrate(click);
			}

			@Override
			protected void onCancel(ClickType click) {
				PreviewCrateGUI.super.open();
			}
		}.open();
	}

	private void purchaseCrate(ClickType clickType) {
		ICrateKeys crateKeys = ICrateKeys.getByUser(this.user);
		int keys = crateKeys.getKeys(crate.getId());

		if (keys > 0) {
			crateKeys.removeKeys(crate.getId(), 1);
			try {
				new RouletteCrateGUI(getUser(), crate).open();
			} catch (PermissionException exception) {
				user.sendMessage(exception.getMessage());
			}
			return;
		}

		if (crate.price.amount() <= 0) {
			user.sendMessage("This crate can not be purchased.");
			return;
		}

		IBalances balances = IBalances.getByUser(user);

		if (!balances.has(crate.price)) {
			user.sendMessage("You don't have enough money to open this crate."); // TODO config
			return;
		}

		balances.remove(crate.price, "CRATE");
		try {
			new RouletteCrateGUI(getUser(), crate).open();
		} catch (PermissionException exception) {
			user.sendMessage(exception.getMessage());
		}
	}

	public void openMainMenu(ClickType clickType) {
		new CratesGUI(getUser()).open();
	}
}
