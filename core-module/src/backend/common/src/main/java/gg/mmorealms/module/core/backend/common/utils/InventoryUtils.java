package gg.mmorealms.module.core.backend.common.utils;

import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.server.level.ServerPlayer;

public class InventoryUtils {

	public static int getFreeSlots(User user) {
		return getFreeSlots(user.getPlayer());
	}

	public static int getFreeSlots(ServerPlayer player) {
		int freeSlots = 0;
		for (int i = 0; i < player.getInventory().items.size(); i++) {
			if (player.getInventory().items.get(i).isEmpty()) {
				freeSlots++;
			}
		}
		return freeSlots;
	}

	public static boolean hasFullInventory(User user) {
		return hasFullInventory(user.getPlayer());
	}

	public static boolean hasFullInventory(ServerPlayer player) {
		return getFreeSlots(player) <= 0;
	}
}
