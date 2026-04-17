package gg.mmorealms.loader.common.dto;

import gg.mmorealms.loader.common.exception.SyncedRequestException;

public abstract class SimpleSyncedNetworkObject<Identifier, ObjectClass> extends SyncedNetworkObject<Identifier, ObjectClass, Void> {
	public SimpleSyncedNetworkObject(Class<ObjectClass> objectClass) {
		super(objectClass, 2);
	}

	public abstract String getDestinationServer();

	@Override
	public String getDestinationServer(Void destinationServerFinder) {
		return getDestinationServer();
	}

	protected <T> T sendRequest(Object... args) throws SyncedRequestException {
		return sendRequest(getDestinationServer(), args);
	}
}
