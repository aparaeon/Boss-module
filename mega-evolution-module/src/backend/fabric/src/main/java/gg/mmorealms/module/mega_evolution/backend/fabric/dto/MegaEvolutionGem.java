package gg.mmorealms.module.mega_evolution.backend.fabric.dto;

import gg.mmorealms.module.core.common.utils.StringUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionDataKeys;
import lombok.Getter;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;

import java.util.function.Predicate;

@Getter
public class MegaEvolutionGem {

    private final String gemName;
    private final String gemDisplayName;

    private final String stoneOreName;
    private final String stoneOreDisplayName;

    private final String deepslateOreName;
    private final String deepslateOreDisplayName;

    private final int veinEveryChunk;
    private final int minHeight;
    private final int maxHeight;
    private final Predicate<BiomeSelectionContext> biomeSelector;

    public MegaEvolutionGem(
            String gemName,
            int veinEveryChunk,
            int minHeight,
            int maxHeight,
            Predicate<BiomeSelectionContext> biomeSelector
    ) {
        this.gemName = gemName;
        this.gemDisplayName = formatDisplayName(gemName);

        this.stoneOreName = gemName + MegaEvolutionDataKeys.STONE_ORE_KEY;
        this.stoneOreDisplayName = gemDisplayName + " Ore";

        this.deepslateOreName = gemName + MegaEvolutionDataKeys.DEEPSLATE_ORE_KEY;
        this.deepslateOreDisplayName = "Deepslate " + gemDisplayName + " Ore";

        this.veinEveryChunk = veinEveryChunk;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.biomeSelector = biomeSelector;
    }

    public MegaEvolutionGem(String gemName) {
        this(gemName, 64, -144, 16, BiomeSelectors.foundInOverworld());
    }

    private static String formatDisplayName(String raw) {
        String replaced = raw.replace('_', ' ');
        return StringUtils.toTitleCase(replaced);
    }

}