package gg.mmorealms.module.essentials.backend.common.dto.user_settings;

import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;

@NoArgsConstructor
public class FlySetting implements IUserSetting<ServerPlayer> {

	public boolean value = false;

	@Override
	public void apply(ServerPlayer player) {
		player.getAbilities().flying = value;
		player.getAbilities().mayfly = value;
		player.onUpdateAbilities();
	}
}
