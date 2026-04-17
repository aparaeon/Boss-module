package gg.mmorealms.module.hunts.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.gui.HuntsGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@Command(aliases = {"hunts", "hunt"}, onlyFor = Command.OnlyFor.PLAYERS)
public class HuntsCommand extends UserCommand {
    public HuntsCommand(CommonCommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
        new HuntsGUI(user).open();
    }
}
