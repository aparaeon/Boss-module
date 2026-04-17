package gg.mmorealms.module.store.backend.common.gui;

import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import gg.mmorealms.module.store.backend.common.dto.StoreEntry;
import gg.mmorealms.module.store.backend.common.dto.database.StoreTransaction;

import java.util.List;

public class CheckoutGUI extends GUI {

	private final IUser target;
	private final StoreEntry entry;
	private final int discount;
	private final Lambda backExecutor;

	public CheckoutGUI(User user, IUser target, StoreEntry entry, int discount, Lambda backExecutor) {
		super(
			user,
			new Settings().chestSize(6)
		);

		this.target = target;
		this.entry = entry;
		this.discount = discount;
		this.backExecutor = backExecutor;
	}

	@Override
	public void setup() {
		setButton(GUIButton.empty()
			.position(5, 0, 3, 1))
			.displayName("Go back")
			.onClick(backExecutor);

		setButton(GUIButton.empty()
			.position(5, 6, 3, 1))
			.displayName("Buy")
			.onClick(this::confirm);
	}

	@Override
	public String getTitleString() {
		return "\uF80A\uF260\uF900\uF7C6" + entry.getImage();
	}

	public void confirm() {
		IBalances balances = IBalances.getByUser(this.user);
		Price price = this.entry.getPrice().discount(this.discount);

		if (!balances.has(price)) {
			this.user.sendMessage("You do not have enough money to buy this!"); // TODO Config
			return;
		}

		balances.remove(price, "STORE");

		List<String> commands = this.entry.getCommands()
			.parse("user", this.target.getUsername())
			.parse();

		for (String command : commands) {
			StoreBackendModule.instance().executeCommand(command);
		}

		Logger.log(new MessageBuilder("User {user} bought {entry} for {price} for user {target}")
			.parse("user", this.user.getUUID())
			.parse("entry", this.entry.getId())
			.parse("price", price.toString())
			.parse("target", this.target.getUUID())
			.parse());

		new StoreTransaction(
			entry,
			this.user.getUUID(),
			this.target.getUUID()
		);

		close();
	}
}
