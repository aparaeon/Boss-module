package gg.mmorealms.module.breeding.backend.fabric.commands;


import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.utils.NeuterUtils;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.command.IPokemonPartyCommand;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"neuter"}, arguments = {"slot", "neutered"}, onlyFor = Command.OnlyFor.PLAYERS)
public class NeuterCommand extends UserCommand implements IPokemonPartyCommand {

    private @Inject BreedingConfig breedingConfig;

    public NeuterCommand(CommonCommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
        if (argument.equals(IPokemon.NEUTERED_KEY)) {
            return List.of("true", "false");
        }

        return getPokemonPartyAutoComplete(argument);
    }

    @Override
    protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
        String slotString = arguments.get(0);
        String neuteredString = arguments.get(1);

        IPokemon slotPokemon = getPokemonInSlot(user, slotString);
        if (slotPokemon != null && slotPokemon.getNative() instanceof Pokemon pokemon) {
            boolean neutered = Boolean.parseBoolean(neuteredString);
            NeuterUtils.setNeutered(user, pokemon, neutered);
        }
    }
}
