package gg.mmorealms.module.chat_games.velocity.dto.user_settings;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatGamesNotificationSetting implements IUserSetting<Player> {

	private boolean enabled = true;

	public void toggle() {
		this.enabled = !this.enabled;
	}

	@Override
	public void apply(Player player) {
		// nop
	}

	@Override
	public void cleanup(Player player) {
		// nop
	}

}
