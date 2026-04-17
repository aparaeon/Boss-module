package gg.mmorealms.module.essentials.backend.common.dto;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InventorySlot {

	public Type type;
	public int slot;
	public JsonElement itemJson;

	public enum Type {
		PLAYER_INVENTORY,
		PLAYER_HOTBAR,
		PLAYER_ARMOUR,
		PLAYER_OFFHAND,
		PLAYER_ENDER_CHEST,
	}

}
