package gg.mmorealms.module.boss.backend.fabric.manager;

import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.common.event.BossSpawnEvent;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleWonEvent;
@OnlyOn(servers = ServerType.WILD)
public class BossListener {

	public BossListener() {
	}

	@EventHandler
	public void onBossSpawn(BossSpawnEvent ev) {
		BossFabricModule mod = BossFabricModule.instance();
		BossSpawner spawner = mod != null ? mod.getBossSpawner() : null;
		if (spawner != null) {
			spawner.handleSpawnRequest(ev); // already hops to main internally
		}
	}

	@EventHandler
	public void onBattleWon(BattleWonEvent ev) {
		BossFabricModule mod = BossFabricModule.instance();
		if (mod == null) return;
		BossManager manager = mod.getBossManager();
		if (manager == null) return;
		mod.runOnMain(() -> {
			manager.handleBattleWon(ev);
			if (manager.hasPendingDespawns()) {
				manager.retryPendingDespawns();
			}
		});
	}
}
