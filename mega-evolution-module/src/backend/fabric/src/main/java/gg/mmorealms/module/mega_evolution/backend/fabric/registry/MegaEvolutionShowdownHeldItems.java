package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import com.cobblemon.mod.common.api.reactive.SimpleObservable;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.utils.StringUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.interfaces.IShowdownDataRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MegaEvolutionShowdownHeldItems implements IShowdownDataRegistry {
    private final String idPath = "showdown/data/mods/cobblemon/items";
    private final String functionName = "receiveHeldItemData";
    private final SimpleObservable<MegaEvolutionShowdownHeldItems> observable = new SimpleObservable<>();

    private final Map<String, String> keyToScript = new HashMap<>();

    @Getter
    @Accessors(fluent = true)
    private static final MegaEvolutionShowdownHeldItems instance = new MegaEvolutionShowdownHeldItems();

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

    private MegaEvolutionShowdownHeldItems() {
        register();
    }

    @Override
    public void reload(@NotNull ResourceManager resourceManager) {
        keyToScript.clear();

        MegaEvolution.streamWithGems().forEach(megaEvolution -> {
            if (megaEvolution.isShowdownInjected()) {
                String itemKey = megaEvolution.getGemName().replace("_", "");
                String heldItem = getMegaEvolutionScript(megaEvolution);
                keyToScript.put(itemKey, heldItem);
            }
        });

        observable.emit(this);
    }

    private String getMegaEvolutionScript(MegaEvolution megaEvolution) {
        String speciesName = StringUtils.toTitleCase(megaEvolution.getSpeciesName());

        return heldItemTemplate
                .parse("gemDisplayName", megaEvolution.getGemDisplayName())
                .parse("gemShowdownName", megaEvolution.getGemShowdownName())
                .parse("speciesName", speciesName)
                .toString();
    }

    @Override
    public void sync(@NotNull ServerPlayer serverPlayer) {

    }

}
