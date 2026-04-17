package gg.mmorealms.module.essentials.backend.common.dto.event;

import com.google.gson.JsonElement;
import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.essentials.backend.common.dto.InventorySlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddItemEvent extends NetworkEvent {

	private UUID targetUUID;
	private JsonElement itemJson;
	private InventorySlot.Type inventoryType;

	public AddItemEvent(String target, UUID targetUUID, JsonElement itemJson, InventorySlot.Type inventoryType) {
		super(target);
		this.targetUUID = targetUUID;
		this.itemJson = itemJson;
		this.inventoryType = inventoryType;
	}

}
