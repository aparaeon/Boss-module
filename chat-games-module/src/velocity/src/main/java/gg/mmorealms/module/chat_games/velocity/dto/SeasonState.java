package gg.mmorealms.module.chat_games.velocity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonState {

	private int currentSeasonId = 0;
	private String currentSeasonName = "";
	private String lastResetDate = "";

}
