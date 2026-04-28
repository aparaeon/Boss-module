package gg.mmorealms.module.chat_games.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.chat_games.common.dto.GeneratedQuestion;
import gg.mmorealms.module.chat_games.common.dto.QuestionType;
import gg.mmorealms.module.chat_games.common.dto.event.ChatGamesSoundEvent;
import gg.mmorealms.module.chat_games.common.dto.event.RequestQuestionEvent;
import gg.mmorealms.module.chat_games.common.utils.AnswerNormalizer;
import gg.mmorealms.module.chat_games.velocity.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.velocity.config.ChatGamesReward;
import gg.mmorealms.module.chat_games.velocity.config.CustomQuestion;
import gg.mmorealms.module.chat_games.velocity.dto.ChatGamesSeasonWins;
import gg.mmorealms.module.chat_games.velocity.dto.user_settings.ChatGamesNotificationSetting;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;
import gg.mmorealms.module.user_data.velocity.database.VelocityUserSettings;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChatGamesManager {

	private @Inject ProxyServer proxy;
	private @Inject ChatGamesConfig config;
	private @Inject ServerManager serverManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject LeaderboardManager leaderboardManager;
	private @Inject SeasonManager seasonManager;

	@Nullable
	private GeneratedQuestion activeQuestion;
	@Nullable
	private String lastQuestionKey;
	private long questionStartTime;
	@Nullable
	private CancelableTimeTask expiryTask;
	private boolean activeSkipRewards;

	private long nextGameTime;
	private CancelableTimeTask timerTask;

	public void start() {
		nextGameTime = System.currentTimeMillis() + randomInterval();
		timerTask = ScheduleUtils.runTaskTimer(this::tick, Time.seconds(1));
	}

	private GeneratedQuestion buildMathQuestion() {
		int operation = RandomUtils.getRandom(new Range(0, 3));

		int num1, num2, answer;
		String questionText;

		if (operation == 0) {
			num1 = RandomUtils.getRandom(config.math.addends);
			num2 = RandomUtils.getRandom(config.math.addends);
			answer = num1 + num2;
			questionText = num1 + " + " + num2;
		} else if (operation == 1) {
			num1 = RandomUtils.getRandom(config.math.addends);
			num2 = RandomUtils.getRandom(new Range(1, num1));
			answer = num1 - num2;
			questionText = num1 + " - " + num2;
		} else if (operation == 2) {
			num1 = RandomUtils.getRandom(config.math.factors);
			num2 = RandomUtils.getRandom(config.math.factors);
			answer = num1 * num2;
			questionText = num1 + " × " + num2;
		} else {
			num2 = RandomUtils.getRandom(config.math.factors);
			answer = RandomUtils.getRandom(config.math.factors);
			num1 = num2 * answer;
			questionText = num1 + " ÷ " + num2;
		}

		String answerStr = String.valueOf(answer);
		return new GeneratedQuestion(QuestionType.MATH.getKey(), questionText, answerStr, Set.of(answerStr));
	}

	private long randomInterval() {
		return Time.minutes(RandomUtils.getRandom(config.intervalMinutes)).toMilliseconds();
	}

	public synchronized boolean hasActiveGame() {
		return activeQuestion != null;
	}

	public synchronized boolean tryAnswer(UUID uuid, String username, String input) {
		if (activeQuestion == null) {
			return false;
		}

		String normalized = AnswerNormalizer.normalize(input);

		Set<String> inputWords = new HashSet<>(Arrays.asList(normalized.split(" ")));
		boolean matched = activeQuestion.answers().stream()
			.anyMatch(answer -> new HashSet<>(Arrays.asList(answer.split(" "))).equals(inputWords));
		if (!matched) {
			return false;
		}

		double answerTimeSeconds = (System.currentTimeMillis() - questionStartTime) / 1000.0;
		int seasonId = seasonManager.getCurrentSeasonId();

		String questionKey = activeQuestion.questionKey();
		String prefix = getPrefix(questionKey);
		boolean isPokemonTypeQuestion = questionKey.equals(QuestionType.POKEMON_TYPE.getKey());
		String displayAnswer = isPokemonTypeQuestion ? activeQuestion.displayAnswer()
			: (input.isEmpty() ? input : Character.toUpperCase(input.charAt(0)) + input.substring(1));
		boolean skipRewards = activeSkipRewards;

		Logger.info("[ChatGames] " + username + " got the answer correct for " + questionKey + " (answer: " + displayAnswer + ")");
		Logger.info("[ChatGames] Added a point for " + username);

		clearActiveGame();

		ScheduleUtils.runTaskAsync(() -> {
			ChatGamesSeasonWins.recordWin(uuid, seasonId);
			leaderboardManager.refresh();
		});

		String broadcastText = config.lang.winnerBroadcast
			.parse("prefix", prefix)
			.parse("player", username)
			.parse("answer", displayAnswer)
			.parse("time", String.format("%.2f", answerTimeSeconds))
			.parse();

		broadcastMessageToPlayers(broadcastText);

		if (!skipRewards) {
			executeRewards(uuid, username);
		}
		nextGameTime = System.currentTimeMillis() + randomInterval();
		return true;
	}

	private synchronized void tick() {
		if (activeQuestion != null) {
			return;
		}
		if (System.currentTimeMillis() < nextGameTime) {
			return;
		}
		triggerGame();
	}

	private synchronized void triggerGame() {
		if (activeQuestion != null) {
			return;
		}

		QuestionType type = RandomUtils.getRandomWeighed(List.of(QuestionType.values()));

		if (!config.customQuestions.isEmpty() && type == QuestionType.CUSTOM) {
			CustomQuestion custom = RandomUtils.getRandom(config.customQuestions);
			Set<String> answers = new HashSet<>();
			for (String answer : custom.answers) {
				answers.add(AnswerNormalizer.normalize(answer));
			}
			activateQuestion(new GeneratedQuestion(QuestionType.CUSTOM.getKey(), custom.question, custom.displayAnswer, answers));
			return;
		}

		if (type == QuestionType.MATH) {
			activateQuestion(buildMathQuestion());
			return;
		}

		EngineServer engineServer = serverManager.getLowestUsageServer(ServerType.REALMS);

		if (engineServer == null) {
			Logger.warn("[ChatGames] No backend server available, rescheduling.");
			nextGameTime = System.currentTimeMillis() + randomInterval();
			return;
		}

		new RequestQuestionEvent(engineServer.getServerID(), lastQuestionKey).send().thenAccept(question -> {
			synchronized (this) {
				if (question == null) {
					Logger.warn("[ChatGames] Received null question from backend, rescheduling.");
					nextGameTime = System.currentTimeMillis() + randomInterval();
					return;
				}

				if (activeQuestion != null) {
					return;
				}

				activateQuestion(question);
			}
		});
	}

	private void activateQuestion(GeneratedQuestion question) {
		activeQuestion = question;
		lastQuestionKey = question.questionKey();
		questionStartTime = System.currentTimeMillis();

		String broadcastText = getBroadcast(question.questionKey())
			.parse("question", question.questionText())
			.parse();

		broadcastQuestionToPlayers(broadcastText);
		expiryTask = ScheduleUtils.runTaskLater(this::onExpire, Time.seconds(config.roundDurationSeconds));
	}

	private String getPrefix(String questionKey) {
		MessageBuilder prefix = config.questionPrefixes.get(questionKey);
		if (prefix == null) {
			prefix = config.questionPrefixes.get("default");
		}
		if (prefix == null) {
			return "<light_purple><bold>✦ Trivia</bold>";
		}
		return prefix.parse();
	}

	private MessageBuilder getBroadcast(String questionKey) {
		MessageBuilder broadcast = config.questionBroadcasts.get(questionKey);
		if (broadcast != null) {
			return broadcast;
		}
		broadcast = config.questionBroadcasts.get("default");
		return broadcast != null ? broadcast : config.fallbackBroadcast;
	}

	private synchronized void onExpire() {
		if (activeQuestion == null) {
			return;
		}

		GeneratedQuestion expiredQuestion = activeQuestion;
		clearActiveGame();

		broadcastMessageToPlayers(config.lang.expiredBroadcast
			.parse("prefix", getPrefix(expiredQuestion.questionKey()))
			.parse("answer", expiredQuestion.displayAnswer())
			.parse()
		);

		nextGameTime = System.currentTimeMillis() + randomInterval();
	}

	public synchronized void force() {
		if (activeQuestion != null) {
			skip();
		}
		triggerGame();
	}

	public synchronized boolean skip() {
		if (activeQuestion == null) {
			return false;
		}

		GeneratedQuestion skippedQuestion = activeQuestion;
		clearActiveGame();

		broadcastMessageToPlayers(config.lang.expiredBroadcast
			.parse("prefix", getPrefix(skippedQuestion.questionKey()))
			.parse("answer", skippedQuestion.displayAnswer())
			.parse()
		);

		nextGameTime = System.currentTimeMillis() + randomInterval();
		return true;
	}

	public synchronized void closeActiveGameForSeasonReset() {
		if (activeQuestion == null) {
			return;
		}

		clearActiveGame();
		nextGameTime = System.currentTimeMillis() + randomInterval();
	}

	public synchronized boolean broadcastQuestion(String questionText, String answer) {
		if (activeQuestion != null) {
			return false;
		}

		activeQuestion = new GeneratedQuestion("custom", questionText, answer, Set.of(AnswerNormalizer.normalize(answer)));
		activeSkipRewards = true;
		lastQuestionKey = "custom";
		questionStartTime = System.currentTimeMillis();

		String broadcastText = getBroadcast("custom")
			.parse("question", questionText)
			.parse();

		broadcastQuestionToPlayers(broadcastText);

		expiryTask = ScheduleUtils.runTaskLater(this::onExpire, Time.seconds(config.roundDurationSeconds));
		return true;
	}

	private void broadcastQuestionToPlayers(String broadcastText) {
		Component parsed = miniMessageManager.parse(broadcastText);
		Map<String, List<UUID>> serverToPlayers = new HashMap<>();
		for (Player player : proxy.getAllPlayers()) {
			VelocityUserSettings settings = VelocityUserSettings.getByUUID(player.getUniqueId());
			if (settings == null) {
				continue;
			}
			if (settings.get(ChatGamesNotificationSetting.class).isEnabled()) {
				player.sendMessage(parsed);
				player.getCurrentServer()
					.map(s -> s.getServerInfo().getName())
					.ifPresent(serverId -> serverToPlayers
						.computeIfAbsent(serverId, k -> new ArrayList<>())
						.add(player.getUniqueId())
					);
			}
		}
		serverToPlayers.forEach((serverId, uuids) -> new ChatGamesSoundEvent(serverId, uuids).send());
	}

	private void broadcastMessageToPlayers(String broadcastText) {
		Component parsed = miniMessageManager.parse(broadcastText);
		for (Player player : proxy.getAllPlayers()) {
			VelocityUserSettings settings = VelocityUserSettings.getByUUID(player.getUniqueId());
			if (settings == null) {
				continue;
			}
			if (settings.get(ChatGamesNotificationSetting.class).isEnabled()) {
				player.sendMessage(parsed);
			}
		}
	}

	private void clearActiveGame() {
		activeQuestion = null;
		activeSkipRewards = false;

		if (expiryTask != null) {
			expiryTask.cancel();
			expiryTask = null;
		}
	}

	private void executeRewards(UUID uuid, String username) {
		if (config.rewards.isEmpty()) {
			Logger.warn("[ChatGames] executeRewards: no rewards configured, skipping");
			return;
		}

		ChatGamesReward reward = RandomUtils.getRandomWeighed(config.rewards);

		List<String> commands = reward.getCommands()
			.parse("player", username)
			.parse();

		String serverId = proxy.getPlayer(uuid)
			.flatMap(Player::getCurrentServer)
			.map(s -> s.getServerInfo().getName())
			.orElse(null);

		if (serverId == null) {
			Logger.warn("[ChatGames] executeRewards: player not on any server, skipping commands");
		} else {
			for (String command : commands) {
				CommandExecuteEvent.onBackend(serverId, command).send();
			}
		}

		StringBuilder rewardText = new StringBuilder(reward.getRewardMessage().parse("player", username).parse());
		for (String line : reward.getRewardLines()) {
			rewardText.append("\n").append(line);
		}
		proxy.getPlayer(uuid).ifPresent(player -> player.sendMessage(miniMessageManager.parse(rewardText.toString())));
	}

}
