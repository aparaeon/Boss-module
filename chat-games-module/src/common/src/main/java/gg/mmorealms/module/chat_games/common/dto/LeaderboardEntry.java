package gg.mmorealms.module.chat_games.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {

	private UUID uuid;
	private String username;
	private long wins;
	private int placement;

}
