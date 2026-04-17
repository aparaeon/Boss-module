package gg.mmorealms.module.core.backend.common.dto;

import com.raduvoinea.utils.generic.utils.NetworkUtils;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;

@Getter
@ToString
public class BackendDetails {

	private final String hostname;
	private final int port;
	private final ServerType serverType;

	public BackendDetails() throws IOException {
		this(new File("server.properties"));
	}

	public BackendDetails(File file) throws IOException {
		this(new ServerProperties(file));
	}

	public BackendDetails(ServerProperties serverProperties) {
		this.hostname = NetworkUtils.getHostname();
		this.port = Integer.parseInt(serverProperties.get("server-port"));
		this.serverType = BackendLoader.instance().getServerType();
	}

}
