package gg.mmorealms.module.essentials.backend.common.dto.user_settings;

import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

@NoArgsConstructor
public class NightVisionSetting implements IUserSetting<ServerPlayer> {

	public boolean value = false;

	@Override
	public void apply(ServerPlayer player) {
		if (value) {
			player.addEffect(new MobEffectInstance(
				MobEffects.NIGHT_VISION,
				Integer.MAX_VALUE,
				0,
				false,
				false
			));
			return;
		}
		player.removeEffect(MobEffects.NIGHT_VISION);
	}

	@Override
	public void cleanup(ServerPlayer player) {
		player.removeEffect(MobEffects.NIGHT_VISION);
	}
}
