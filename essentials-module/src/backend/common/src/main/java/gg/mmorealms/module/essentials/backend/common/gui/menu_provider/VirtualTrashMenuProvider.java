package gg.mmorealms.module.essentials.backend.common.gui.menu_provider;

import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VirtualTrashMenuProvider implements MenuProvider {
	@Override
	public @NotNull Component getDisplayName() {
		return EssentialsBackendModule.instance().getMiniMessageManager().parse("<gold><b>Donator <reset><white>Trash Can"); // TODO Config
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
		return ChestMenu.sixRows(i, inventory);
	}
}
