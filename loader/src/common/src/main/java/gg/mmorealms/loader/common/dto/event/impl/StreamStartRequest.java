package gg.mmorealms.loader.common.dto.event.impl;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamStartRequest extends NetworkRequest<Boolean> {

	private String streamType;
	private String streamID;

	public StreamStartRequest(String target, String streamType, String streamID) {
		super(target);
		this.streamType = streamType;
		this.streamID = streamID;
	}

	public String getStreamChannel() {
		return streamType + "#" + streamID;
	}


}
