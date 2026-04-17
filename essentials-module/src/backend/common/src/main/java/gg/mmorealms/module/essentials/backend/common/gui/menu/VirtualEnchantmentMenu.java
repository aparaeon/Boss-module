package gg.mmorealms.module.essentials.backend.common.gui.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.jetbrains.annotations.NotNull;

public class VirtualEnchantmentMenu extends EnchantmentMenu {

	public VirtualEnchantmentMenu(int containerId, Inventory playerInventory) {
		super(containerId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), playerInventory.player.blockPosition()));
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!player.level().isClientSide) {
			this.clearContainer(player, this.enchantSlots);
		}
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
}
