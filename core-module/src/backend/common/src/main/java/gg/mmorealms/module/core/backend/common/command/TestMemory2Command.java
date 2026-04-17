package gg.mmorealms.module.core.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"test_memory2"}, arguments = {"size"})
public class TestMemory2Command extends BackendCommand {

	private final List<BigObject> bigObjects = new ArrayList<>();

	public TestMemory2Command(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String sizeString = arguments.getFirst();

		int size;
		try {
			size = Integer.parseInt(sizeString);
		} catch (NumberFormatException e) {
			sendMessage(sender, "Invalid size");
			return;
		}

		for (int i = 0; i < size; i++) {
			bigObjects.add(new BigObject());
		}
	}

	@NoArgsConstructor
	public static class BigObject {
		private final byte[] bytes = new byte[1 * 1024 * 1024]; // 1 MB
	}
}
