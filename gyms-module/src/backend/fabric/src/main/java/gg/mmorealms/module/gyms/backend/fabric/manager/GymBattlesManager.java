package gg.mmorealms.module.gyms.backend.fabric.manager;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.dto.enums.GymCooldown;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.dto.gym.GymRegion;
import gg.mmorealms.module.gyms.backend.common.dto.gym.clauses.GymInBattleClauses;
import gg.mmorealms.module.gyms.backend.fabric.GymsFabricModule;
import gg.mmorealms.module.gyms.backend.fabric.dto.BattleInfo;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GymBattlesManager {
	private final Map<ServerPlayer, BattleInfo> activeBattlesMap = new HashMap<>();

	private CancelableTimeTask cleaner;

	public GymBattlesManager() {
		GymsBackendModule instance = GymsBackendModule.instance();
		if (instance.getServerType() != ServerType.GYMS) {
			return;
		}


		for (GymRegion region : instance.getConfig().regions) {
			for (Gym gym : region.getGyms()) {
				gym.reassignTrainer();
				gym.refreshClauses();
			}
		}

		cleaner = ScheduleUtils.runTaskTimer(() -> {
			List<ServerPlayer> playerList = new ArrayList<>();
			for (Map.Entry<ServerPlayer, BattleInfo> entry : activeBattlesMap.entrySet()) {
				if (entry.getValue().forcedEndTime() <= System.currentTimeMillis()) {
					playerList.add(entry.getKey());
				}
			}

			for (ServerPlayer serverPlayer : playerList) {
				forceEndBattle(serverPlayer);
			}
		}, Time.minutes(1));
	}

	public void removeEntry(ServerPlayer player) {
		activeBattlesMap.remove(player);
	}

	public @Nullable BattleInfo getBattle(ServerPlayer player) {
		return activeBattlesMap.get(player);
	}

	public void addBattle(PokemonBattle battle, Gym gym, ServerPlayer player) {
		IBackendCooldowns cooldowns = IBackendCooldowns.getByPlayer(player);
		cooldowns.set(GymCooldown.loss(gym.getId()), gym.getLossCooldown().toMilliseconds() + Time.minutes(5).toMilliseconds());

		System.currentTimeMillis();

		activeBattlesMap.put(player, new BattleInfo(battle, gym, GymsBackendModule.instance().getConfig().battleConfig.battleTime));
	}

	private void forceEndBattle(ServerPlayer player) {
		BattleInfo battleInfo = activeBattlesMap.get(player);
		if (battleInfo == null) {
			return;
		}

		battleInfo.battle().end();
		player.sendSystemMessage(GymsFabricModule.instance().getMiniMessageManager().parse("<red>Your battle took to long!"));
		activeBattlesMap.remove(player);
	}

	public @Nullable GymInBattleClauses getClauses(ServerPlayer player) {
		BattleInfo battleInfo = activeBattlesMap.get(player);

		if (battleInfo == null) {
			return null;
		}

		return battleInfo.gym().getGymInBattleClauses();
	}
}