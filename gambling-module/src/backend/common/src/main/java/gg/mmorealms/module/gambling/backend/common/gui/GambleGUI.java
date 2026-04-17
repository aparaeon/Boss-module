package gg.mmorealms.module.gambling.backend.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.chat.backend.common.manager.BackendChatInputManager;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.gambling.backend.common.GamblingBackendModule;
import gg.mmorealms.module.gambling.backend.common.dto.RouletteSlotType;
import gg.mmorealms.module.gambling.backend.common.files.GambleConfig;

public class GambleGUI extends GUI {

	private static GambleConfig BASE_CONFIG = GamblingBackendModule.instance().getConfig();
	private static GambleConfig.GambleGUI CONFIG = BASE_CONFIG.gambleGUI;

	private RouletteSlotType slotType = RouletteSlotType.UNKNOWN;
	private CurrencyType currencyType = CurrencyType.POKECOINS;
	private double amount = 0;

	public GambleGUI(User user) {
		super(
				user,
				new Settings().chestSize(6)
		);
	}

	@Override
	public String getTitleString() {
		String offset1 = "\uF7FC";
		String offset2 = "\uF7FE";
		String offset3 = "\uF7FE";
		String button1 = offset1 + (slotType == RouletteSlotType.RED ? "\uF274" : "\uF271");
		String button2 = offset2 + (slotType == RouletteSlotType.GREEN ? "\uF275" : "\uF272");
		String button3 = offset3 + (slotType == RouletteSlotType.BLACK ? "\uF276" : "\uF273");

		return "\uF80A\uF270\uF900" + button1 + button2 + button3;
	}

	@Override
	public void setup() {
		setButton(CONFIG.redButton).onClick(() -> updateSlotType(RouletteSlotType.RED));
		setButton(CONFIG.greenButton).onClick(() -> updateSlotType(RouletteSlotType.GREEN));
		setButton(CONFIG.blackButton).onClick(() -> updateSlotType(RouletteSlotType.BLACK));

		setButton(CONFIG.setCurrencyButton)
				.onClick(this::updateCurrencyType)
				.placeholder("currency", currencyType.getName());

		setButton(CONFIG.setBetButton)
				.placeholder("amount", NumberUtils.formatNumberWithDecimalPlaces(amount, 2))
				.placeholder("currency", currencyType.getName())
				.onClick(this::openAmountSelection);

		setButton(CONFIG.playButton)
				.placeholder("amount", NumberUtils.formatNumberWithDecimalPlaces(amount, 2))
				.placeholder("currency", currencyType.getName())
				.placeholder("slot_type", slotType.getFriendlyName())
				.onClick(this::play);
	}

	private void updateSlotType(RouletteSlotType slotType) {
		this.slotType = slotType;
		refresh();
	}

	private void updateCurrencyType() {
		this.currencyType = BASE_CONFIG.allowedCurrencies.get((BASE_CONFIG.allowedCurrencies.indexOf(this.currencyType) + 1) % BASE_CONFIG.allowedCurrencies.size());
		this.amount = 0;
		refresh();
	}

	private void openAmountSelection() {
		GamblingBackendModule.instance().getChatInputManager().registerChatCapture(new BackendChatInputManager.ChatCapture(this.user, "Please enter the amount you want to bet") { // TODO Config
			@Override
			protected boolean execute(String input) {
				try {
					amount = Integer.parseInt(input);
				} catch (NumberFormatException e) {
					amount = 0;
					return false;
				}

				if (!checkBalance()) {
					amount = 0;
					return false;
				}

				refresh();
				open(true);
				return true;
			}
		});
	}

	private boolean checkBalance() {
		IBalances balances = IBalances.getByUser(this.user);

		if (!balances.has(this.currencyType, this.amount)) {
			this.user.sendMessage(new MessageBuilder("<red><b>You do not have enough {currency} to place this bet! You currently have {balance} {currency}.") // TODO Config
					.parse("currency", this.currencyType.getName())
					.parse("balance", NumberUtils.formatNumberWithDecimalPlaces(this.amount, 2))
					.parse()
			);
			return false;
		}

		return true;
	}

	private void play() {
		if (this.slotType == RouletteSlotType.UNKNOWN) {
			this.user.sendMessage("<red>Please select a color"); // TODO Config
			return;
		}

		if (this.amount <= 0) {
			this.user.sendMessage("<red>Please set a valid bet amount"); // TODO Config
			return;
		}

		if (!checkBalance()) {
			return;
		}

		IBalances balances = IBalances.getByUser(this.user);
		balances.remove(this.currencyType, this.amount, "GAMBLE_ROULETTE");

		new RouletteGUI(this.user, this.slotType, this.amount, this.currencyType).open();
	}
}
