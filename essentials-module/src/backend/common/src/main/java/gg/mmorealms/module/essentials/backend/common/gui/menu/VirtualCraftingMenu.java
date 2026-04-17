package gg.mmorealms.module.essentials.backend.common.gui.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.NotNull;

public class VirtualCraftingMenu extends CraftingMenu {

	public VirtualCraftingMenu(int containerId, Inventory playerInventory) {
		super(containerId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), playerInventory.player.blockPosition()));
	}

	@Override
	public void removed(@NotNull Player player) {
		super.removed(player);
		if (!player.level().isClientSide) {
			this.clearContainer(player, this.craftSlots);
		}
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
}
