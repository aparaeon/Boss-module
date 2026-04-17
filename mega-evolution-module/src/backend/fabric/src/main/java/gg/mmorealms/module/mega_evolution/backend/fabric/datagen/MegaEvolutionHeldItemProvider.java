package gg.mmorealms.module.mega_evolution.backend.fabric.datagen;

import com.google.common.hash.Hashing;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.utils.StringUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MegaEvolutionHeldItemProvider implements DataProvider {

    protected final FabricDataOutput output;

    public MegaEvolutionHeldItemProvider(FabricDataOutput output) {
        this.output = output;
    }

    private final MessageBuilder heldItemTemplate = new MessageBuilder("""
            {
                name: "{gemDisplayName}",
                spritenum: 666,
                megaStone: "{gemShowdownName}",
                megaEvolves: "{speciesName}",
                itemUser: ["{speciesName}"],
                onTakeItem(item, source) {
                    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
                    return true;
                },
                num: -999,
                gen: 9,
                isNonstandard: "Past"
            }
            """);

    private String getMegaEvolutionScript(MegaEvolution megaEvolution) {
        String speciesName = StringUtils.toTitleCase(megaEvolution.getSpeciesName());

        return heldItemTemplate
                .parse("gemDisplayName", megaEvolution.getGemDisplayName())
                .parse("gemShowdownName", megaEvolution.getGemShowdownName())
                .parse("speciesName", speciesName)
                .toString();
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput output) {
        PackOutput.PathProvider provider = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "held_items");

        List<CompletableFuture<?>> futures = new ArrayList<>();

        MegaEvolution.streamWithGems().forEach(evolution -> {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath("cobblemon", evolution.getGemName());
            Path path = provider.file(location, "js");
            Logger.debug("Generating file: " + path.toAbsolutePath());

            String script = getMegaEvolutionScript(evolution);
            futures.add(saveUtf8(output, script, path));
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static CompletableFuture<?> saveUtf8(CachedOutput output, String content, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                var hash = Hashing.sha1().hashBytes(bytes);
                output.writeIfNeeded(path, bytes, hash);
            } catch (Exception e) {
                LOGGER.error("Failed to save JS file to {}", path, e);
            }
        }, Util.backgroundExecutor());
    }

    @Override
    public @NotNull String getName() {
        return "MegaEvolutionHeldItemProvider";
    }
}
