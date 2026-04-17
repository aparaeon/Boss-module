package gg.mmorealms.module.breeding.backend.fabric.commands;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.utils.PokemonEggUtils;
import gg.mmorealms.module.core.backend.common.command.ICooldownCommand;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.command.IPokemonPartyCommand;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import lombok.Getter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"breed", "breeding"}, arguments = {"slot1", "slot2"}, onlyFor = Command.OnlyFor.PLAYERS)
public class BreedingCommand extends UserCommand implements ICooldownCommand, IPokemonPartyCommand {

    private @Inject BreedingConfig breedingConfig;

    @Getter
    private final String COMMAND_COOLDOWN_KEY = "BREEDING_COOLDOWN";

    public BreedingCommand(CommonCommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
        return getPokemonPartySlots();
    }

    @Override
    protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
        if (isCommandOnCooldown(user)) {
            return;
        }

        String slotString1 = arguments.get(0);
        String slotString2 = arguments.get(1);

        if (slotString1.equals(slotString2)) {
            user.sendMessage(breedingConfig.lang.samePokemonMessage);
            return;
        }

        IPokemon slotPokemon1 = getPokemonInSlot(user, slotString1);
        if (slotPokemon1 == null) {
            return;
        }

        IPokemon slotPokemon2 = getPokemonInSlot(user, slotString2);
        if (slotPokemon2 == null) {
            return;
        }

        Pokemon pokemon1 = (Pokemon) slotPokemon1.getNative();
        Pokemon pokemon2 = (Pokemon) slotPokemon2.getNative();

        ItemStack pokemonEgg = PokemonEggUtils.generateEgg(pokemon1, pokemon2);
        if (pokemonEgg == null) {
            MessageBuilder messageBuilder = breedingConfig.lang.notCompatiblePokemonMessage
                    .parse("pokemonName1", slotPokemon1.getName())
                    .parse("pokemonName2", slotPokemon2.getName());

            user.sendMessage(messageBuilder);
            return;
        }

        setCommandCooldown(user, breedingConfig.breedingCommandCooldown);

        MessageBuilder messageBuilder = breedingConfig.lang.breedMessage
                .parse("pokemonEggName", pokemonEgg.getDisplayName().getString());

        user.sendMessage(messageBuilder);

        user.getPlayer().addItem(pokemonEgg);
    }
}
