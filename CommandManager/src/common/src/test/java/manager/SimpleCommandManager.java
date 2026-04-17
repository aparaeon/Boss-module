package manager;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.reflections.Reflections;
import dto.CommandSender;
import dto.SimpleConsole;
import dto.SimplePlayer;
import org.jetbrains.annotations.NotNull;

public class SimpleCommandManager extends CommonCommandManager {

	public SimpleCommandManager(Reflections.@NotNull Crawler reflectionsCrawler, @NotNull CommandManagerConfig config) {
		super(reflectionsCrawler, SimplePlayer.class, SimpleConsole.class, CommandSender.class, config, Holder.empty());
	}

	@Override
	protected void platformRegister(@NotNull CommonCommand command) {

	}

	@Override
	public void sendMessage(Object target, String message) {
		((CommandSender) target).sendMessage(message);
	}

}
