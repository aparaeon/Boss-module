package gg.mmorealms.module.gyms.backend.fabric.helper;

import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.dto.gym.clauses.GymInBattleClauses;
import gg.mmorealms.module.gyms.backend.fabric.GymsFabricModule;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class MixinCommon {
	public static boolean bagClauseVerification(Player player) {
		if (!PlayerExtensionsKt.isInBattle((ServerPlayer) player)) {
			return true;
		}

		GymInBattleClauses result = GymsFabricModule.instance().getGymBattlesManager().getClauses((ServerPlayer) player);
		if (result == null || !result.isBagClause()) {
			return true;
		}

		IUser.getByUUID(player.getUUID()).sendMessage(GymsBackendModule.instance().getConfig().lang.failedBagClauseMessage);
		return false;
	}
}