package gg.mmorealms.module.limbo.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.module.limbo.velocity.files.LimboConfig;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;

@Command(aliases = {"transfer_to_limbo"}, onlyFor = Command.OnlyFor.PLAYERS)
public class TransferToLimbo extends VelocityCommand {

	private static final int BATCH_SIZE = 25;
	private static final Time BATCH_TIMEOUT = Time.seconds(10);

	private @Inject ProxyServer server;
	private @Inject LimboConfig config;

	public TransferToLimbo(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		ScheduleUtils.runTaskAsync(() -> transfer(player, config.getLimboServerAddress()));
	}

	@SneakyThrows(InterruptedException.class)
	private void transfer(Player sender, InetSocketAddress targetAddress) {
		int count = 0;
		int currentBatch = 0;
		int maxBatches = server.getAllPlayers().size() / BATCH_SIZE + 1;

		while (server.getAllPlayers().size() > 1 || currentBatch <= maxBatches) {
			count += BATCH_SIZE;
			currentBatch++;

			List<Player> batch = server.getAllPlayers().stream()
					.limit(BATCH_SIZE)
					.toList();

			for (Player targetPlayer : batch) {
				if (targetPlayer.getUniqueId().equals(sender.getUniqueId())) {
					continue;
				}

				targetPlayer.transferToHost(targetAddress);
			}

			sendMessage(sender, new MessageBuilder("Transferred {count} players. Waiting {timeout} seconds before next batch...")
					.parse("count", count)
					.parse("timeout", BATCH_TIMEOUT.toSeconds())
			);

			//noinspection BusyWait
			Thread.sleep(BATCH_TIMEOUT.toMilliseconds());
		}
	}
}
