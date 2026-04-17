package gg.mmorealms.module.scoreboard.backend.common.dto;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.scoreboard.backend.common.ScoreboardBackendModule;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class ScoreboardObjective {

	private static final String OBJ_PREFIX = "pi_";

	private static String serverName = null;

	private final String name;
	private final UUID uuid;

	public ScoreboardObjective(UUID uuid) {
		this.name = OBJ_PREFIX + uuid.toString().replace("-", "").substring(0, 13);
		this.uuid = uuid;
		Logger.debug("Creating scoreboard for " + this.uuid + " with name " + this.name);

		if (serverName == null) {
			String normalName = ScoreboardBackendModule.instance().getServerType().getDisplayName();
			String sanitizedName = ScoreboardBackendModule.instance().getMiniMessageManager().sanitize(normalName);
			String capitalizedName = sanitizedName.substring(0, 1).toUpperCase() + sanitizedName.substring(1);
			serverName = normalName.replace(sanitizedName, capitalizedName);
		}
	}

	public Objective toNativeObjective() {
		Objective objective = this.getScoreboard().getObjective(this.name);

		if (objective == null) {
			objective = this.getScoreboard().addObjective(
				this.name,
				ObjectiveCriteria.DUMMY,
				ScoreboardBackendModule.instance().getMiniMessageManager().parse("<b><red>MMO <aqua>REALMS</b>"),
				ObjectiveCriteria.RenderType.INTEGER,
				true,
				null
			);
		}

		return objective;
	}

	public void destroy() {
		Scoreboard scoreboard = this.getScoreboard();
		Objective nativeObjective = scoreboard.getObjective(this.name);
		if (nativeObjective != null) {
			scoreboard.removeObjective(nativeObjective);
		}
	}

	public void update() {
		ServerPlayer player = this.getPlayer();
		if (player == null) {
			return;
		}

		Objective nativeObjective = this.toNativeObjective();
		IBalances balances = IBalances.getByUUID(this.uuid);

		// TODO Config
		List<String> rows = new MessageBuilderList(List.of(
			"",
			"",
			" <white>✦ <white>Rank: {rank}",
			" {poke_coins_format}</bold>▶ <white>PokeCoins: {poke_coins_format}</bold>{poke_coins}",
			" {gems_format}</bold>▶ <white>Gems: {gems_format}</bold>{gems}",
			"",
			" <white><b>◈</b> <white>{server_type} <gray>| <aqua>play.mmorealms.gg"
		))
			.parse("rank", LuckPermsUtils.getPrefix(this.uuid))
			.parse("poke_coins_format", CurrencyType.POKECOINS.getColor())
			.parse("poke_coins", balances.get(CurrencyType.POKECOINS).intValue())
			.parse("gems_format", CurrencyType.GEMS.getColor())
			.parse("gems", balances.get(CurrencyType.GEMS).intValue())
			.parse("server_type", serverName)
			.parse();

		for (int i = 0; i < rows.size(); i++) {
			String stableKey = "§" + Integer.toHexString(i) + "§r";
			int scoreValue = 99 - i;
			String row = rows.get(i);

			sendScore(player, nativeObjective, stableKey, scoreValue, ScoreboardBackendModule.instance().getMiniMessageManager().parse(row));
		}
	}

	public void sendScore(ServerPlayer player, Objective objective, String stableKey, int value, Component displayName) {
		player.connection.send(new ClientboundSetScorePacket(
			stableKey,
			objective.getName(),
			value,
			Optional.of(displayName),
			Optional.of(new FixedFormat(Component.empty()))
		));
	}

	public Scoreboard getScoreboard() {
		return ScoreboardBackendModule.instance().getServer().getScoreboard();
	}

	private ServerPlayer getPlayer() {
		return ScoreboardBackendModule.instance().getServer().getPlayerList().getPlayer(this.uuid);
	}
}