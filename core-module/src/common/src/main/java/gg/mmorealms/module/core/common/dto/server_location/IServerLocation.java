package gg.mmorealms.module.core.common.dto.server_location;

import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import gg.mmorealms.loader.common.dto.ServerType;

public interface IServerLocation extends ISerializable {

	static IServerLocation of(String serverID) {
		return new ExactServerLocation(serverID);
	}

	static IServerLocation of(ServerType serverType) {
		return new ServerTypeLocation(serverType);
	}

	String getServer();

}
