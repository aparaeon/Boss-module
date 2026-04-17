package gg.mmorealms.module.essentials.backend.common.gui.menu_provider;

import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import gg.mmorealms.module.essentials.backend.common.gui.menu.VirtualEnchantmentMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VirtualEnchantingTableMenuProvider implements MenuProvider {
	@Override
	public @NotNull Component getDisplayName() {
		return EssentialsBackendModule.instance().getMiniMessageManager().parse("<gold><b>Donator <reset><white>Enchanting Table"); // TODO Config
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
		return new VirtualEnchantmentMenu(i, inventory);
	}
}
