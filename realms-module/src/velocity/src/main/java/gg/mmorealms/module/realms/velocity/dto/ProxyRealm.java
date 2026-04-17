package gg.mmorealms.module.realms.velocity.dto;

import com.raduvoinea.utils.file_manager.utils.DateUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.realms.common.dto.RealmState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProxyRealm {

	private String serverID;
	private UUID ownerUUID;
	private RealmState state;
	private long stateChangeTimestamp;

	public ProxyRealm(String serverID, UUID ownerUUID) {
		this.serverID = serverID;
		this.ownerUUID = ownerUUID;
		this.updateState(RealmState.LOADING);
	}

	public void updateState(RealmState newState) {
		Logger.debug(new MessageBuilder("Realm {owner_uuid} changing state from {old_state} to {new_state}")
				.parse("owner_uuid", this.getOwnerUUID())
				.parse("old_state", this.getState())
				.parse("new_state", newState)
		);
		this.state = newState;
		this.stateChangeTimestamp = System.currentTimeMillis();
	}

	public String dump() {
		MessageBuilder realmEntryTemplate = new MessageBuilder("    - {owner_uuid}: {state} (since {since})"); // TODO Config

		return realmEntryTemplate
				.parse("owner_uuid", this.getOwnerUUID())
				.parse("state", this.getState())
				.parse("since", DateUtils.convertUnixTimeToDate(this.getStateChangeTimestamp()))
				.parse();
	}

}
