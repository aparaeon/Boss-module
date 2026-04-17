package gg.mmorealms.module.kits.backend.common.menu;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KitAddMenu extends ChestMenu {
	private static final int SIZE = 4 * 9;
	private static final int DISPLAY_ITEM_SLOT = 40;
	private final String name;
	private final Time cooldown;
	private final int slot;

	public KitAddMenu(int syncId, Inventory playerInventory, SimpleContainer container
			, String name, int slot, Time cooldown) {
		super(MenuType.GENERIC_9x5, syncId, playerInventory, container, 5);

		this.name = name;
		this.slot = slot;
		this.cooldown = cooldown;

		for (int j = 36; j < 45; j++) {
			if (j == 40) {
				continue;
			}

			this.getSlot(j).set(new ItemStack(Items.BARRIER, 1));
		}

	}

	@Override
	public void removed(@NotNull Player player) {
		super.removed(player);
		List<ItemStack> items = new ArrayList<>();

		for (int i = 0; i < SIZE; i++) {
			ItemStack stack = getContainer().getItem(i);
			if (!stack.isEmpty()) {
				items.add(stack);
			}
		}

		ItemStack stack = getContainer().getItem(DISPLAY_ITEM_SLOT);

		KitsConfig config = KitsBackendModule.instance().getConfig();

		IUser user = IUser.getByUUID(player.getUUID());
		if (KitUtils.add(new Kit(name, slot, cooldown, items, stack))) {
			user.sendMessage(config.lang.successCreateMessage.parse("name", name));
			return;
		}
		user.sendMessage(config.lang.failedCreateMessage.parse("name", name));
	}

}
