package gg.mmorealms.module.chat_games.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public class RequestQuestionEvent extends NetworkRequest<GeneratedQuestion> {

	private @Nullable String lastQuestionKey;

	public RequestQuestionEvent(String targetServerId, @Nullable String lastQuestionKey) {
		super(targetServerId);
		this.lastQuestionKey = lastQuestionKey;
	}

}
