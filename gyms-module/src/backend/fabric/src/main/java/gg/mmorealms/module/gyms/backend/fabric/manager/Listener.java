package gg.mmorealms.module.gyms.backend.fabric.manager;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCBattleActor;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.dto.cooldown.BackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.CooldownInfo;
import gg.mmorealms.module.gyms.backend.common.dto.database.GymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.enums.GymCooldown;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.dto.gym.clauses.GymPreBattleClause;
import gg.mmorealms.module.gyms.backend.common.manager.GymUtils;
import gg.mmorealms.module.gyms.backend.fabric.GymsFabricModule;
import gg.mmorealms.module.gyms.backend.fabric.dto.BattleInfo;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleStartRequest;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleWonEvent;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.EndBattleEvent;
import gg.mmorealms.module.pokemon.common.dto.Response;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

@OnlyOn(servers = ServerType.GYMS)
// Currently implemented to work only for singles gym battle format
public class Listener {
	@EventHandler
	public void onBattleWonEvent(BattleWonEvent event) {
		if (event.getLosers().getFirst() instanceof NPCBattleActor trainer &&
				event.getWinners().getFirst() instanceof PlayerBattleActor playerActor) {
			processWin(trainer, playerActor);
			return;
		}

		if (event.getWinners().getFirst() instanceof NPCBattleActor trainer &&
				event.getLosers().getFirst() instanceof PlayerBattleActor playerActor) {
			processLoss(trainer, playerActor);
			return;
		}

		Logger.error("Battle is not a gym trainer battle");
	}

	private void processWin(NPCBattleActor trainer, PlayerBattleActor playerBattleActor) {
		Gym gym = GymUtils.getGymByNPC(trainer.getNpc());
		if (gym == null) {
			Logger.debug("No Gym found based on the losing NPC trainer");
			return;
		}

		IUserGymRecord userGymRecord = IUserGymRecord.get(playerBattleActor.getUuid());
		IUser user = IUser.getByUUID(playerBattleActor.getUuid());

		user.sendMessage(gym.getPlayerWonBattleMessage()
				.parse("user", user.getUsername())
				.parse("trainer", trainer.getName().getString())
		);

		MinecraftServer server = GymsFabricModule.instance().getServer();
		CommandSourceStack commandSourceStack = server.createCommandSourceStack();

		userGymRecord.logWin(gym.getId());
		GymRecord gymRecord = userGymRecord.getGymRecord(gym.getId());
		if (gymRecord == null || gymRecord.wins() <= 1) {
			for (String command : gym.getFirstTimeBonusRewards()) {
				server.getCommands().performPrefixedCommand(commandSourceStack,
						new MessageBuilder(command)
								.parse("user", user.getUsername())
								.parse("uuid", user.getUUID())
								.parse()
				);
			}
		}

		for (String command : gym.getRegularRewardCommands()) {
			server.getCommands().performPrefixedCommand(commandSourceStack,
					new MessageBuilder(command)
							.parse("user", user.getUsername())
							.parse("uuid", user.getUUID())
							.parse()
			);
		}

		IBackendCooldowns cooldowns = IBackendCooldowns.getByUUID(playerBattleActor.getUuid());
		if (cooldowns == null) {
			cooldowns = new BackendCooldowns(user.getUUID());
		}

		cooldowns.set(GymCooldown.win(gym.getId()), gym.getWinCooldown());

		user.sendMessage(GymsBackendModule.instance().getConfig().lang.prePlayerRewardMessage);
		if (gymRecord == null || gymRecord.wins() <= 1) {
			Logger.info(new MessageBuilder("[FIRST WIN] {name} ({uuid}) has defeated trainer {trainer}")
					.parse("name", playerBattleActor.getName().getString())
					.parse("uuid", playerBattleActor.getUuid())
					.parse("trainer", trainer.getName().getString())
			);

			for (String message : gym.getFirstWinPlayerMessage()) {
				user.sendMessage(message);
			}
			return;
		}

		Logger.info(new MessageBuilder("[REPEAT WIN] {name} ({uuid}) has defeated trainer {trainer}")
				.parse("name", playerBattleActor.getName().getString())
				.parse("uuid", playerBattleActor.getUuid())
				.parse("trainer", trainer.getName().getString())
		);

		for (String message : gym.getRegularWinPlayerMessage()) {
			user.sendMessage(message);
		}
	}

	private void processLoss(NPCBattleActor trainer, PlayerBattleActor playerBattleActor) {
		Gym gym = GymUtils.getGymByNPC(trainer.getNpc());
		if (gym == null) {
			Logger.debug("No Gym found based on the winning NPC trainer");
			return;
		}

		IUser user = IUser.getByUUID(playerBattleActor.getUuid());

		user.sendMessage(gym.getPlayerLostBattleMessage()
				.parse("user", user.getUsername())
				.parse("trainer", trainer.getName().getString())
		);

		IUserGymRecord userGymRecord = IUserGymRecord.get(playerBattleActor.getUuid());
		userGymRecord.logLoss(gym.getId());

		IBackendCooldowns cooldowns = IBackendCooldowns.getByUser(user);

		cooldowns.set(GymCooldown.loss(gym.getId()), gym.getLossCooldown());
	}

	@EventHandler(order = 100000)
	public void onBattleStartRequest(BattleStartRequest request) {
		GymsConfig config = GymsBackendModule.instance().getConfig();
		PokemonBattle battle = request.getBattle();
		NPCBattleActor trainer = null;
		PlayerBattleActor playerActor = null;
		for (BattleActor actor : battle.getActors()) {
			if (actor instanceof NPCBattleActor npcBattleActor) {
				trainer = npcBattleActor;
				continue;
			}

			if (actor instanceof PlayerBattleActor playerBattleActor) {
				playerActor = playerBattleActor;
			}
		}

		if (trainer == null || playerActor == null) {
			Logger.error("Did not found trainer or player:");
			Logger.error(trainer + " " + playerActor);
			return;
		}


		Gym gym = GymUtils.getGymByNPC(trainer.getNpc());
		if (gym == null) {
			Logger.debug("No Gym found based on the NPC trainer");
			return;
		}

		IBackendCooldowns cooldowns = IBackendCooldowns.getByUUID(playerActor.getUuid());
		if (cooldowns != null) {
			CooldownInfo activeCooldownInfo = gym.getActiveCooldownInfo(playerActor.getUuid());
			if (activeCooldownInfo != null) {
				request.setResult(new Response(false, config.lang.playerCooldown
						.parse("action", activeCooldownInfo.getCooldownDisplay())
						.parse("cooldown", activeCooldownInfo.getFormattedTime())
				));
				return;
			}
		}

		Response checkResult = gym.canBattle(playerActor.getUuid());
		if (!checkResult.isAccepted()) {
			request.setResult(checkResult);
			return;
		}

		List<GymPreBattleClause> gymPreBattleClauses = gym.getGymPreBattleClauses();
		if (gymPreBattleClauses == null) {
			Logger.error("No Gym clauses found");
			return;
		}

		if (gymPreBattleClauses.isEmpty()) {
			Logger.error("Gym clauses empty");
			return;
		}

		ServerPlayer player = playerActor.getEntity();
		if (player == null) {
			request.setResult(new Response(false, "Player disconnected"));
			return;
		}

		IPokemonParty pokemonParty = PokemonBackendModule.instance().getPlatformImplementation().getParty(player);
		for (GymPreBattleClause gymPreBattleClause : gymPreBattleClauses) {
			if (!gymPreBattleClause.clause().run(pokemonParty)) {
				request.setResult(new Response(false, gymPreBattleClause.failMessage()));
				return;
			}
		}

		GymsFabricModule.instance().getGymBattlesManager().addBattle(battle, gym, player);
	}

	@EventHandler
	public void onEndBattleEvent(EndBattleEvent event) {
		IUserGymRecord userGymRecord = IUserGymRecord.get(event.getPlayer());
		BattleInfo battle = GymsFabricModule.instance().getGymBattlesManager().getBattle(event.getPlayer());

		if (battle == null) {
			return;
		}

		Gym gym = battle.gym();
		userGymRecord.logLoss(gym.getId());

		IBackendCooldowns cooldowns = IBackendCooldowns.getByPlayer(event.getPlayer());
		cooldowns.set(GymCooldown.loss(gym.getId()), gym.getLossCooldown());
		GymsFabricModule.instance().getGymBattlesManager().removeEntry(event.getPlayer());
	}
}