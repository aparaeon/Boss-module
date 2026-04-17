package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import com.cobblemon.mod.common.api.reactive.SimpleObservable;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IShowdownDataRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class MegaEvolutionShowdownAbilities implements IShowdownDataRegistry {
    private final String idPath = "showdown/data/mods/cobblemon/abilities";
    private final String functionName = "receiveAbilityData";
    private final Map<String, String> keyToScript = new HashMap<>();
    private final SimpleObservable<MegaEvolutionShowdownAbilities> observable = new SimpleObservable<>();

    @Getter
    @Accessors(fluent = true)
    private static final MegaEvolutionShowdownAbilities instance = new MegaEvolutionShowdownAbilities();

    private MegaEvolutionShowdownAbilities() {
        register();
    }

    @Override
    public void reload(@NotNull ResourceManager resourceManager) {
        keyToScript.clear();

        Predicate<ResourceLocation> jsFilter = resourceLocation ->
                resourceLocation.getPath().endsWith("js");

        Map<ResourceLocation, Resource> resources = resourceManager.listResources(getIdPath(), jsFilter);

        resources.forEach((resourceLocation, resource) -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                String fileName = new File(resourceLocation.getPath()).getName().replace(".js", "");
                String fileContent = reader.lines().collect(Collectors.joining("\n"));

                Logger.debug("Found ability file: " + fileName);

                keyToScript.put(fileName, fileContent);
            } catch (IOException e) {
                Logger.error("Failed to read file while reloading abilities: " + e.getMessage());
            }
        });

        observable.emit(this);
    }

    @Override
    public void sync(@NotNull ServerPlayer serverPlayer) {

    }
}
