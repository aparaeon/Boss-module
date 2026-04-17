package gg.mmorealms.module.breeding.backend.fabric.items;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.dto.ParentData;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingBlocks;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingComponents;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingDataKeys;
import gg.mmorealms.module.breeding.backend.fabric.utils.PokemonEggUtils;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.utils.SoundUtils;
import gg.mmorealms.module.core.backend.fabric.items.PolymerModelBlockItem;
import gg.mmorealms.module.core.backend.fabric.registry.CoreDataComponents;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerItemUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;



public class PokemonEggItem extends PolymerModelBlockItem {

    public PokemonEggItem() {
        super(PolymerItemUtils.itemModel(BreedingDataKeys.POKEMON_EGG_KEY, Items.IRON_GOLEM_SPAWN_EGG),
                BreedingBlocks.POKEMON_EGG,
                new Properties()
                        .stacksTo(1)
                        .component(BreedingComponents.STEPS, 0f)
                        .component(BreedingComponents.STEPS_GOAL, 0f)
                        .component(BreedingComponents.FATHER, new ParentData())
                        .component(BreedingComponents.MOTHER, new ParentData())
                        .component(BreedingComponents.POKEMON_PROPERTIES, new PokemonProperties())
                        .component(CoreDataComponents.NO_RENAME, true));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack heldItem = player.getItemInHand(interactionHand);
        if (PokemonEggUtils.canHatch(heldItem)) {
            return hatchEgg(level, player, heldItem);
        }

        return InteractionResultHolder.pass(heldItem);
    }

    private InteractionResultHolder<ItemStack> hatchEgg(Level level, Player player, ItemStack pokemonEgg) {
        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.fail(pokemonEgg);
        }

        PokemonEggUtils.hatchEgg(serverPlayer, pokemonEgg);

        SoundUtils.playSound(level, player.blockPosition(), SoundEvents.TURTLE_EGG_CRACK);

        return InteractionResultHolder.success(pokemonEgg);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        Component unknownName = Component.literal(config.egg.unknownEggName);

        PokemonProperties pokemonProperties = itemStack.get(BreedingComponents.POKEMON_PROPERTIES);
        if (pokemonProperties == null) {
            return unknownName;
        }

        String speciesName = pokemonProperties.getSpecies();
        if (speciesName == null) {
            return unknownName;
        }

        GUIButton pokemonEggItem = config.egg.pokemonEggItem;

        MessageBuilder pokemonName = new MessageBuilder(pokemonEggItem.getDisplayName())
                .parse("pokemonName", StringUtils.capitalize(speciesName));

        return BreedingFabricModule.instance()
                .getMiniMessageManager()
                .parse(pokemonName);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack,
                                @NotNull TooltipContext tooltipContext,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag tooltipFlag) {

        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        ParentData father = itemStack.getOrDefault(BreedingComponents.FATHER, new ParentData());
        ParentData mother = itemStack.getOrDefault(BreedingComponents.MOTHER, new ParentData());

        String fatherGender = config.egg.genderMap.get(father.gender());
        String motherGender = config.egg.genderMap.get(mother.gender());

        int steps = itemStack.getOrDefault(BreedingComponents.STEPS, 0).intValue();
        int stepsGoal = itemStack.getOrDefault(BreedingComponents.STEPS_GOAL, 0).intValue();

        String stepsStr = String.valueOf(Math.min(steps, stepsGoal));
        String stepsGoalStr = String.valueOf(stepsGoal);

        boolean canHatch = PokemonEggUtils.canHatch(itemStack);

        String hatchInstruction = canHatch
                ? config.egg.hatchInstruction
                : "";

        GUIButton pokemonEggItem = config.egg.pokemonEggItem;

        MessageBuilderList tooltipList = new MessageBuilderList(pokemonEggItem.getLore())
                .parse("father", father.name())
                .parse("mother", mother.name())
                .parse("fatherGender", fatherGender)
                .parse("motherGender", motherGender)
                .parse("steps", stepsStr)
                .parse("stepsGoal", stepsGoalStr)
                .parse("hatchInstruction", hatchInstruction);

        List<Component> tooltipComponents = BreedingFabricModule.instance()
                .getMiniMessageManager()
                .parse(tooltipList);

        tooltip.addAll(tooltipComponents);
    }

}
