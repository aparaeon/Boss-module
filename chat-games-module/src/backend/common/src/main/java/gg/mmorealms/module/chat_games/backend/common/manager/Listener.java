package gg.mmorealms.module.chat_games.backend.common.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.module.chat_games.backend.common.gui.LeaderboardGUI;
import gg.mmorealms.module.chat_games.backend.common.gui.RewardClaimGUI;
import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import gg.mmorealms.module.chat_games.common.dto.QuestionType;
import gg.mmorealms.module.chat_games.common.dto.event.ChatGamesSoundEvent;
import gg.mmorealms.module.chat_games.common.dto.event.OpenLeaderboardEvent;
import gg.mmorealms.module.chat_games.common.dto.event.OpenRewardClaimEvent;
import gg.mmorealms.module.chat_games.common.dto.event.RequestQuestionEvent;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Listener {

	private @Inject PokemonQuestionGenerator questionGenerator;

	@EventHandler
	public void onChatGamesSound(ChatGamesSoundEvent event) {
		MinecraftServer server = BackendLoader.instance().getServer();
		for (UUID uuid : event.getPlayerUuids()) {
			ServerPlayer player = server.getPlayerList().getPlayer(uuid);
			if (player != null) {
				//noinspection resource
				player.connection.send(new ClientboundSoundPacket(
					Holder.direct(SoundEvents.EXPERIENCE_ORB_PICKUP),
					SoundSource.MASTER,
					player.getX(), player.getY(), player.getZ(),
					0.7f, 1.0f,
					player.level().getRandom().nextLong()
				));
			}
		}
	}

	@EventHandler
	public void onRequestQuestion(RequestQuestionEvent event) {
		QuestionType chosenType = questionGenerator.pickWeightedRandom(event.getLastQuestionKey());
		@Nullable GeneratedQuestion question = questionGenerator.generate(chosenType);

		if (question == null) {
			Logger.warn("[ChatGames] Failed to generate question of type: " + chosenType.name());
			event.setResult(null);
			return;
		}

		event.setResult(question);
	}

	@EventHandler
	public void onOpenLeaderboard(OpenLeaderboardEvent event) {
		MinecraftServer server = BackendLoader.instance().getServer();
		ServerPlayer player = server.getPlayerList().getPlayer(event.getPlayerUuid());

		if (player == null) {
			return;
		}

		User user = IUser.getByPlayer(player);
		LeaderboardGUI.open(user, event.getOverallEntries(), event.getSeasonEntries(), event.isSeason(), event.getSeasonRewardLore());
	}

	@EventHandler
	public void onOpenRewardClaim(OpenRewardClaimEvent event) {
		MinecraftServer server = BackendLoader.instance().getServer();
		ServerPlayer player = server.getPlayerList().getPlayer(event.getPlayerUuid());

		if (player == null) {
			Logger.warn("[ChatGames] OpenRewardClaimEvent: player " + event.getPlayerUuid()
				+ " not found on this backend — GUI not opened.");
			return;
		}

		User user = IUser.getByPlayer(player);
		new RewardClaimGUI(user, event).open();
	}

}
