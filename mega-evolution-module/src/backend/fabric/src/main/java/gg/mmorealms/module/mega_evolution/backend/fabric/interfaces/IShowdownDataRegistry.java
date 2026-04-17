package gg.mmorealms.module.mega_evolution.backend.fabric.interfaces;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.data.DataRegistry;
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService;
import com.cobblemon.mod.relocations.graalvm.polyglot.Value;
import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import kotlin.Unit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IShowdownDataRegistry extends DataRegistry {
    String getIdPath();
    String getFunctionName();
    Map<String, String> getKeyToScript();

    @Override
    default @NotNull ResourceLocation getId() {
        return ResourceUtils.modResource(getIdPath());
    }

    @Override
    default @NotNull PackType getType() {
        return PackType.SERVER_DATA;
    };

    default void register() {
        getObservable().subscribe(Priority.NORMAL, this::registerData);
    }

    default void registerData(DataRegistry dataRegistry) {
        Cobblemon.INSTANCE.getShowdownThread().queue(showdownService -> {
            if (showdownService instanceof GraalShowdownService graalShowdownService) {
                Value receiveHeldItemDataFn = graalShowdownService
                        .getContext()
                        .getBindings("js")
                        .getMember(getFunctionName());

                getKeyToScript().forEach(receiveHeldItemDataFn::execute);
            }

            return Unit.INSTANCE;
        });
    }
}
