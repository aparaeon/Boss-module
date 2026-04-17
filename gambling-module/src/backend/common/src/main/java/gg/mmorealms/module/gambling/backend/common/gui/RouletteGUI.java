package gg.mmorealms.module.gambling.backend.common.gui;

import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.gambling.backend.common.GamblingBackendModule;
import gg.mmorealms.module.gambling.backend.common.dto.RouletteSlotType;
import gg.mmorealms.module.gambling.backend.common.files.GambleConfig;
import net.minecraft.world.item.Items;

import java.util.Random;

public class RouletteGUI extends GUI {

	private static final GambleConfig BASE_CONFIG = GamblingBackendModule.instance().getConfig();
	private static final GambleConfig.RouletteGUI CONFIG = BASE_CONFIG.rouletteGUI;

	private int ticksSinceUpdate;
	private int progress;
	private final int initialOffset;
	private boolean rewarded = false;

	private final double amount;
	private final CurrencyType currencyType;
	private final RouletteSlotType betSlot;

	public RouletteGUI(User user, RouletteSlotType betSlot, double amount, CurrencyType currencyType) {
		super(
				user,
				new Settings().chestSize(6)
		);

		this.initialOffset = new Random().nextInt(CONFIG.allSlots.size());
		this.amount = amount;
		this.currencyType = currencyType;
		this.betSlot = betSlot;

		Logger.log(new MessageBuilder("User {user} is betting {amount} {currency} on {slotType} in roulette. Winning color will be {winning_color}.")
				.parse("user", this.user.getUsername())
				.parse("amount", String.format("%.2f", this.amount))
				.parse("currency", this.currencyType.getName())
				.parse("slotType", this.betSlot.name())
				.parse("winning_color", getSlotType(getWinningSlot()).name())
				.parse()
		);
	}

	@Override
	public void setup() {
		for (Integer redSlot : CONFIG.redSlots) {
			setButton(new GUIButton().display(Items.RED_STAINED_GLASS_PANE), redSlot);
		}

		for (Integer blackSlot : CONFIG.blackSlots) {
			setButton(new GUIButton().display(Items.BLACK_STAINED_GLASS_PANE), blackSlot);
		}

		for (Integer greenSlot : CONFIG.greenSlots) {
			setButton(new GUIButton().display(Items.GREEN_STAINED_GLASS_PANE), greenSlot);
		}

		update();
	}

	private void update() {
		setButton(
				new GUIButton().display(
						switch (getSlotType(getPreviousSlot())) {
							case RED -> Items.RED_STAINED_GLASS_PANE;
							case BLACK -> Items.BLACK_STAINED_GLASS_PANE;
							case GREEN -> Items.GREEN_STAINED_GLASS_PANE;
							case UNKNOWN -> Items.BARRIER;
						}
				),
				getPreviousSlot()
		);

		setButton(
				new GUIButton()
						.display(Items.NETHER_STAR),
				getCurrentSlot()
		);
	}


	@Override
	public String getTitleString() {
		return "Roulette";
	}

	@Override
	public void onTick() {
		this.ticksSinceUpdate++;

		if (rewarded) {
			return;
		}

		if (this.progress >= CONFIG.getMaxProgress()) {
			reward();
			ScheduleUtils.runTaskLater(
					this::close,
					BASE_CONFIG.autoCloseDelay
			);
			return;
		}

		for (Pair2<Range, Integer> pair : CONFIG.animationDelays) {
			Range range = pair.first();
			Integer delay = pair.second();

			if (this.ticksSinceUpdate >= delay && range.contains(this.progress)) {
				this.progress++;
				refresh();
				this.ticksSinceUpdate = 0;
				return;
			}
		}
	}

	@Override
	public void onClose() {
		if (rewarded) {
			return;
		}

		reward();
	}

	private void reward() {
		rewarded = true;
		boolean won = getSlotType(getWinningSlot()) == this.betSlot;

		log(won);

		if (won) {
			double winAmount = CONFIG.winMultipliers.get(this.betSlot) * this.amount;

			IBalances balances = IBalances.getByUser(user);
			balances.add(this.currencyType, winAmount, "GAMBLE_ROULETTE");
			this.user.sendMessage(new MessageBuilder("<green><b>You won {amount} {currency}!") // TODO Config
					.parse("amount", String.format("%.2f", winAmount - this.amount))
					.parse("currency", this.currencyType.getName())
					.parse()
			);
			return;
		}

		this.user.sendMessage(new MessageBuilder("<red><b>You lost {amount} {currency}!") // TODO Config
				.parse("amount", String.format("%.2f", this.amount))
				.parse("currency", this.currencyType.getName())
				.parse()
		);

	}

	private void log(boolean won) {
		Logger.log(new MessageBuilder("User {user} {verb} {amount} {currency} in roulette.")
				.parse("verb", won ? "won" : "lost")
				.parse("user", this.user.getUsername())
				.parse("amount", String.format("%.2f", this.amount))
				.parse("currency", this.currencyType.getName())
				.parse()
		);
	}

	public int getPreviousSlot() {
		return CONFIG.allSlots.get((this.initialOffset + this.progress - 1 + CONFIG.allSlots.size()) % CONFIG.allSlots.size());
	}

	public int getCurrentSlot() {
		return CONFIG.allSlots.get((this.initialOffset + this.progress) % CONFIG.allSlots.size());
	}

	public int getWinningSlot() {
		return CONFIG.allSlots.get((this.initialOffset + CONFIG.getMaxProgress()) % CONFIG.allSlots.size());
	}

	public RouletteSlotType getSlotType(int slot) {
		if (CONFIG.redSlots.contains(slot)) {
			return RouletteSlotType.RED;
		} else if (CONFIG.blackSlots.contains(slot)) {
			return RouletteSlotType.BLACK;
		} else if (CONFIG.greenSlots.contains(slot)) {
			return RouletteSlotType.GREEN;
		}

		return RouletteSlotType.UNKNOWN;
	}

}
