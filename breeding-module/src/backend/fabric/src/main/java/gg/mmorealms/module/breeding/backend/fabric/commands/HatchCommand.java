package gg.mmorealms.module.breeding.backend.fabric.commands;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingItems;
import gg.mmorealms.module.breeding.backend.fabric.utils.PokemonEggUtils;
import gg.mmorealms.module.core.backend.common.command.ICooldownCommand;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@Command(aliases = "hatch", onlyFor = Command.OnlyFor.PLAYERS)
public class HatchCommand extends UserCommand implements ICooldownCommand {

    @Getter
    private final String COMMAND_COOLDOWN_KEY = "HATCH_COOLDOWN";

    private @Inject BreedingConfig breedingConfig;

    public HatchCommand(CommonCommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
        if (isCommandOnCooldown(user)) {
            return;
        }

        ServerPlayer player = user.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offhandItem = player.getOffhandItem();

        ItemStack pokemonEgg = null;

        if (mainHandItem.is(BreedingItems.POKEMON_EGG)) {
            pokemonEgg = mainHandItem;
        } else if (offhandItem.is(BreedingItems.POKEMON_EGG)) {
            pokemonEgg = offhandItem;
        }

        if (pokemonEgg == null) {
            user.sendMessage(breedingConfig.lang.notHoldingPokemonEggMessage);
            return;
        }

        PokemonEggUtils.hatchEgg(player, pokemonEgg);

        setCommandCooldown(user, breedingConfig.hatchCommandCooldown);
        // No need for hatch message, as hatching gives Pokémon, which Cobblemon already announces
    }
}
