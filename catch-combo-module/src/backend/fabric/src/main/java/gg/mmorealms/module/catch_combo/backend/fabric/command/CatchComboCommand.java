package gg.mmorealms.module.catch_combo.backend.fabric.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.catch_combo.backend.fabric.config.CatchComboConfig;
import gg.mmorealms.module.catch_combo.backend.fabric.database.ICatchCombo;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"combo", "catchcombo"}, onlyFor = Command.OnlyFor.PLAYERS)
public class CatchComboCommand extends UserCommand {
    @Inject private CatchComboConfig config;

    public CatchComboCommand(CommonCommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected void executeUser(@NotNull User user, @NotNull List<String> list) {
        ICatchCombo catchCombo = ICatchCombo.get(user);
        MessageBuilder messageBuilder = catchCombo.hasCombo()
                ? config.lang.comboCommandMessage
                : config.lang.noComboCommandMessage;

        MessageBuilder message = catchCombo.parseComboMessage(messageBuilder);
        user.sendMessage(message);
    }

}
