package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerJoinEvent;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.event.impl.UserPreJoinRequest;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.pokemon.backend.common.dto.database.StarterData;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.EndBattleEvent;
import net.minecraft.core.RegistryAccess;

public class Listener {

	private @Inject RegistryAccess registryAccess;

	@EventHandler(order = -400_000)
	private void onPlayerJoin(PlayerJoinEvent event) {
		if(CommonLoader.DUMMY_MODE){
			Logger.warn("Trying to check if player " + event.getPlayer().getName() + " has selected a starter while in dummy mode, skipping database check");
			return;
		}

		DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
			String hql = "FROM user_starter WHERE uuid = :uuid";
			StarterData result = session.createQuery(hql, StarterData.class)
					.setParameter("uuid", event.getPlayer().getUUID())
					.uniqueResult();

			if (result != null) {
				Cobblemon.playerDataManager.getGenericData(event.getPlayer()).setStarterSelected(true);
			}
		});
	}

	@EventHandler(order = -1000)
	public void onPreJoin(UserPreJoinRequest request) {
		PokemonStoreFactory.removeFromCache(request.getUuid());
	}

	@EventHandler
	public void onEndBattleEvent(EndBattleEvent event) {
		PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(event.getPlayer());

		if (battle == null) {
			return;
		}

		battle.end();
	}
}
