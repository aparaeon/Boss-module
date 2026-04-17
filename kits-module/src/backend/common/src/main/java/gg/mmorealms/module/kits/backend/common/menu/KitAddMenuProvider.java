package gg.mmorealms.module.kits.backend.common.menu;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import lombok.AllArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class KitAddMenuProvider implements MenuProvider {
	private final String name;
	private final int slot;
	private final Time cooldown;

	public static KitAddMenuProvider createKit(String kitName, int slot, Time cooldown) {
		return new KitAddMenuProvider(kitName, slot, cooldown);
	}

	@Override
	public @NotNull Component getDisplayName() {
		return KitsBackendModule.instance().getMiniMessageManager().parse(
				KitsBackendModule.instance().getConfig().lang.creatingKit
						.parse("name", name)
						.parse());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
		return new KitAddMenu(i, inventory, new SimpleContainer(9 * 5), name, slot, cooldown);
	}
}
