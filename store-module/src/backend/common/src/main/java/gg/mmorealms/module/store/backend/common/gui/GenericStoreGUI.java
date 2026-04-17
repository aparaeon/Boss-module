package gg.mmorealms.module.store.backend.common.gui;

import gg.mmorealms.module.chat.backend.common.manager.BackendChatInputManager;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.PagedGUI;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import gg.mmorealms.module.store.backend.common.dto.StoreEntry;
import gg.mmorealms.module.store.backend.common.files.StoreConfig;

import java.util.List;

public abstract class GenericStoreGUI<Entry extends StoreEntry> extends PagedGUI {

	private static final StoreConfig CONFIG = StoreBackendModule.instance().getConfig();

	private final String selfBackground;
	private final String giftBackground;

	protected IUser target;

	public GenericStoreGUI(User user, IUser target, String selfBackground, String giftBackground) {
		super(
				user,
				new Settings().chestSize(6)
		);
		this.target = target;

		this.selfBackground = selfBackground;
		this.giftBackground = giftBackground;
	}

	@Override
	public void setup() {
		List<Entry> entries = this.getEntriesOnPage();
		int discount = this.getDiscount();
		String discountString;

		if (discount == 0) {
			discountString = "{price}";
		} else {
			discountString = CONFIG.lang.discountTemplate.parse();
		}

		if (entries.size() >= 1) {
			Entry entry = entries.get(0);

			setButton(entry.getGuiButton()
					.position(0, 0, 3, 4))
					.placeholder("price_tag", discountString)
					.placeholder("old_price", entry.getPrice())
					.placeholder("price", entry.getPrice().discount(discount))
					.onClick((__) -> this.buy(entry, discount));
		}
		if (entries.size() >= 2) {
			Entry entry = entries.get(1);

			setButton(entry.getGuiButton()
					.position(0, 3, 3, 4))
					.placeholder("price_tag", discountString)
					.placeholder("old_price", entry.getPrice())
					.placeholder("price", entry.getPrice().discount(discount))
					.onClick((__) -> this.buy(entry, discount));
		}
		if (entries.size() >= 3) {
			Entry entry = entries.get(2);

			setButton(entry.getGuiButton()
					.position(0, 6, 3, 4))
					.placeholder("price_tag", discountString)
					.placeholder("old_price", entry.getPrice())
					.placeholder("price", entry.getPrice().discount(discount))
					.onClick((__) -> this.buy(entry, discount));
		}

		// buy gems
		setButton(GUIButton.empty()
				.position(4, 0, 3, 1))
				.displayName("Buy Gems")
				.onClick(this::showStore);
		setButton(GUIButton.empty()
				.position(4, 6, 3, 1))
				.displayName("Buy Gems")
				.onClick(this::showStore);

		// upgrade
		if (!isGifting()) {
			setButton(GUIButton.empty()
					.position(4, 3, 3, 2))
					.onClick(this::gift);
		}

		setButton(GUIButton.empty()
				.position(5, 0, 3, 1))
				.displayName("Back")
				.onClick(this::previousPage);

		setButton(GUIButton.empty()
				.position(5, 6, 3, 1))
				.displayName("Next")
				.onClick(this::nextPage);
	}

	public void showStore(ClickType click) {
		this.user.sendMessage(CONFIG.lang.storeMessage);
	}

	@Override
	public String getTitleString() {
		List<Entry> entries = this.getEntriesOnPage();

		String entry1 = entries.size() >= 1 ? "\uF7FC" + entries.get(0).getImage() : "";
		String entry2 = entries.size() >= 2 ? "\uF7FE" + entries.get(1).getImage() : "";
		String entry3 = entries.size() >= 3 ? "\uF7FE" + entries.get(2).getImage() : "";

		String background = isGifting() ? giftBackground : selfBackground;

		return "\uF80A" + background + "\uF900" + entry1 + entry2 + entry3;
	}

	@Override
	protected int getPagesCount() {
		return (int) Math.ceil(getAllEntries().size() / 3.0);
	}

	protected abstract int getDiscount();

	protected abstract List<Entry> getAllEntries();

	private List<Entry> getEntriesOnPage() {
		List<Entry> entries = getAllEntries();

		return entries.subList(
				this.getPage() * 3,
				Math.min((this.getPage() + 1) * 3, entries.size())
		);
	}

	private void buy(Entry entry, int discount) {
		IBalances balances = IBalances.getByUser(this.user);
		Price realPrice = entry.getPrice().discount(discount);

		if (!balances.has(realPrice)) {
			this.user.sendMessage("You do not have enough money to buy this!"); // TODO Config
			return;
		}

		new CheckoutGUI(user, target, entry, getDiscount(), this::open).open();
	}

	private void gift(ClickType click) {
		close();
		StoreBackendModule.instance().getChatInputManager().registerChatCapture(new BackendChatInputManager.ChatCapture(
				this.user,
				"<gray><b>Please enter the username of the user you want to gift this item to or type '<green><b>cancel<reset><gray><b>' to cancel operation." // TODO Config
		) {
			@Override
			protected boolean execute(String input) {
				if (input.equals("cancel")) {
					return true;
				}

				IUser targetUser = IUser.getByUsername(input);

				if (targetUser == null) {
					user.sendMessage("User not found!");
					return false;
				}

				target = targetUser;
				refresh();
				open(true);
				return true;
			}
		});
	}

	public boolean isGifting() {
		return !target.getUUID().equals(user.getUUID());
	}
}
