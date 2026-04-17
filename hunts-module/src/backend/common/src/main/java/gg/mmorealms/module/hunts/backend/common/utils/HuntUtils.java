package gg.mmorealms.module.hunts.backend.common.utils;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class HuntUtils {

    private HuntUtils() { }

    public static void awardPlayer(HuntType type, ServerPlayer player) {

        HuntsConfig config = HuntsBackendModule.instance().getConfig();
        List<String> commands = config.gui.rows.get(type).commands();
        User user = User.get(player);

        MinecraftServer server = HuntsBackendModule.instance().getServer();
        int permissionLevel = 4;

        CommandSourceStack source = server
                .createCommandSourceStack()
                .withSuppressedOutput()
                .withPermission(permissionLevel);

        for (String command : commands) {
            String parsed = new MessageBuilder(command)
                    .parse("user", user.getUsername())
                    .parse();

            server.getCommands().performPrefixedCommand(source, parsed);
        }

    }
}
