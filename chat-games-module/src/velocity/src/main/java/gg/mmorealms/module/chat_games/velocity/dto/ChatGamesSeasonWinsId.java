package gg.mmorealms.module.chat_games.velocity.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatGamesSeasonWinsId implements Serializable {

	private UUID uuid;
	private int seasonId;

}
