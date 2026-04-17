package gg.mmorealms.module.core.common.dto.server_location;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import gg.mmorealms.loader.common.dto.ServerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ServerTypeLocation implements IServerLocation {

	private final String class_name = ServerTypeLocation.class.getName();
	private ServerType serverType;

	// TODO Find a better way
	private static @Setter ReturnArgLambda<String, ServerType> fetchServerType = ignored -> null;

	@Override
	public String getServer() {
		return fetchServerType.run(serverType);
	}

	@Override
	public String toString() {
		return "type '" + serverType + "'";
	}
}
