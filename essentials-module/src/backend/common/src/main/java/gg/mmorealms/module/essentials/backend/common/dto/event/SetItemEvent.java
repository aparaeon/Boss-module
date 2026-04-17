package gg.mmorealms.module.essentials.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.essentials.backend.common.dto.InventorySlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetItemEvent extends NetworkEvent {

	private UUID targetUUID;
	private InventorySlot slot;

	public SetItemEvent(String target, UUID targetUUID, InventorySlot slot) {
		super(target);
		this.targetUUID = targetUUID;
		this.slot = slot;
	}

}
