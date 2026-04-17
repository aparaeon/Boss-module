package gg.mmorealms.module.hunts.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.hunts.backend.common.command.HuntsCommand;
import gg.mmorealms.module.hunts.backend.common.dto.database.IHunts;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@Command(aliases = {"clear"}, arguments = {"target"}, parent = HuntsCommand.class)
public class ClearCommand extends BackendCommand {
    public ClearCommand(CommonCommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
        return recommendPlayersList();
    }

    @Override
    protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
        if (arguments.isEmpty()) {
            return;
        }

        IUser target = IUser.getByUsername(arguments.get(0));
        if (target == null) {
            return;
        }

        IHunts.get(target).clearActiveHunt();
    }
}
