package gg.mmorealms.module.mega_evolution.backend.fabric.items;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.fabric.items.PolymerNamedItem;
import gg.mmorealms.module.mega_evolution.backend.fabric.MegaEvolutionFabricModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.config.MegaEvolutionConfig;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MegaBraceletItem extends PolymerNamedItem {

    public MegaBraceletItem(String modelPath) {
        this(modelPath, new Properties().stacksTo(1));
    }

    public MegaBraceletItem(String modelPath, Properties properties) {
        super("Mega Bracelet", modelPath, Items.DIAMOND, properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack,
                                                           @NotNull Player player,
                                                           @NotNull LivingEntity livingEntity,
                                                           @NotNull InteractionHand interactionHand) {

        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!(livingEntity instanceof PokemonEntity pokemonEntity)) {
            return InteractionResult.PASS;
        }

        if (pokemonEntity.getOwnerUUID() != player.getUUID()) {
            return InteractionResult.PASS;
        }

        // Should account for when already is mega evolving, as we add mega evolution lock
        if (pokemonEntity.isBusy()) {
            return InteractionResult.PASS;
        }

        Pokemon pokemon = pokemonEntity.getPokemon();

        // Always allow devolving
        if (MegaEvolutionUtils.isMegaPokemon(pokemon)) {
            MegaEvolutionUtils.megaDevolve(pokemon);
            return InteractionResult.SUCCESS;
        }

        MegaEvolutionConfig.Lang lang = MegaEvolutionFabricModule.instance().getConfig().lang;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        User user = User.get(serverPlayer);

        List<String> requiredMoves = MegaEvolutionUtils.getRequiredMegaMoveNames(pokemon);

        if (!requiredMoves.isEmpty()) {
            if (!MegaEvolutionUtils.hasRequiredMegaMove(pokemon, requiredMoves)) {
                user.sendActionMessage(lang.noRequiredMegaMove);
                return InteractionResult.FAIL;
            }
        } else if (!MegaEvolutionUtils.isHoldingRequiredGem(pokemon)) {
            user.sendActionMessage(lang.noRequiredMegaGem);
            return InteractionResult.FAIL;
        }

        if (MegaEvolutionUtils.hasMegaPokemonInParty(serverPlayer)) {
            user.sendActionMessage(lang.megaPokemonInPartyMessage);
            return InteractionResult.FAIL;
        }

        if (MegaEvolutionUtils.hasMegaPokemonInPC(serverPlayer)) {
            user.sendActionMessage(lang.megaPokemonInPCMessage);
            return InteractionResult.FAIL;
        }

        if (MegaEvolutionUtils.hasMegaEvolvingPokemonInParty(serverPlayer)) {
            user.sendActionMessage(lang.megaEvolvingPokemonMessage);
            return InteractionResult.FAIL;
        }

        if (!MegaEvolutionUtils.hasRequiredFlower(pokemon)) {
            user.sendActionMessage(lang.megaEvolutionFloetteFlowerMessage);
            return InteractionResult.FAIL;
        }

        MegaEvolution megaEvolution = MegaEvolutionUtils.getPossibleMegaEvolution(pokemon);

        if (megaEvolution == null) {
            return InteractionResult.PASS;
        }

        MegaEvolutionUtils.megaEvolve(pokemon, megaEvolution);

        return InteractionResult.SUCCESS;
    }
}
