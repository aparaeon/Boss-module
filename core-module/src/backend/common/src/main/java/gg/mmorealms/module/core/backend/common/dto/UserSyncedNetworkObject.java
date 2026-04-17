package gg.mmorealms.module.core.backend.common.dto;

import gg.mmorealms.loader.common.dto.SyncedNetworkObject;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;

public abstract class UserSyncedNetworkObject<Identifier, ObjectClass> extends SyncedNetworkObject<Identifier, ObjectClass, IUser> {
	public UserSyncedNetworkObject(Class<ObjectClass> objectClass) {
		super(objectClass, 2);
	}
}
