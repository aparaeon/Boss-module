package gg.mmorealms.module.hunts.backend.common.gui;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.utils.StringUtils;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.HuntData;
import gg.mmorealms.module.hunts.backend.common.dto.HuntPool;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Map;

public class HuntTarget extends HuntButton {

    private final GUIButton placeholderButton;

    public HuntTarget(GUIButton placeholderButton, GUIButton enabledButton, GUIButton disabledButton) {
        super(enabledButton, disabledButton);
        this.placeholderButton = placeholderButton;
    }

    @Override
    protected boolean isEnabled(Hunts hunts, HuntType type) {
        return hunts.isActiveHunt(type);
    }

    @Override
    protected boolean handleClick(ClickType clickType, User user, Hunts hunts, HuntType type) {
        return false;
    }

    @Override
    protected GUIButton getEnabledButton(Hunts hunts, HuntType type) {
        HuntData huntData = hunts.getActiveHuntData();
        if (huntData == null) {
            return GUIButton.empty().position(enabledButton.getPosition());
        }

        String speciesName = huntData.speciesName();
        IPokemon pokemon = IPokemon.getBySpeciesName(speciesName);

        if (pokemon == null) {
            return GUIButton.empty().position(enabledButton.getPosition());
        }

        if (huntData.isShiny()) {
            pokemon.setShiny(true);
        }

        ItemStack pokemonItem = pokemon.toItemStack();
        // Replace displayed item with Pokemon model
        return enabledButton.display(pokemonItem);
    }

    @Override
    protected GUIButton getDisabledButton(Hunts hunts, HuntType type) {
        if (hunts.isHuntOnCooldown(type)) {
            return disabledButton;
        }

        return placeholderButton;
    }

    @Override
    protected Map<String, Object> getEnabledPlaceholders(Hunts hunts, HuntType type) {
        HuntData huntData = hunts.getActiveHuntData();
        if (huntData == null) {
            return Collections.emptyMap();
        }

        // Get display name instead of resource location name stored in HuntsData
        String targetSpecies = huntData.speciesName();
        IPokemon pokemon = IPokemon.getBySpeciesName(targetSpecies);
        String speciesName = (pokemon != null) ? pokemon.getSpeciesName() : "";

        String durationStr = getHuntDurationString(hunts, type);

        String genderName = huntData.genderName();
        genderName = (genderName != null) ? genderName : "";

        String natureName = huntData.natureName();
        natureName = (natureName != null) ? StringUtils.toTitleCase(natureName) : "";

        Integer averageIVs = huntData.averageIVs();
        String averageIVsStr = (averageIVs != null) ? averageIVs.toString() : "";

        return Map.of(
                "target", speciesName,
                "duration", durationStr,
                "gender", genderName,
                "nature", natureName,
                "averageIVs", averageIVsStr);
    }

    @Override
    protected Map<String, Object> getDisabledPlaceholders(Hunts hunts, HuntType type) {
        if (hunts.isHuntOnCooldown(type)) {
            String huntDenyCooldown = hunts.getHuntDenyCooldownFormattedTime(type);
            return Map.of("huntDenyCooldown", huntDenyCooldown);
        }

        String durationStr = getHuntDurationString(hunts, type);
        return Map.of("duration", durationStr);
    }

    private String getHuntDurationString(Hunts hunts, HuntType type) {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        HuntPool huntPool = config.huntPools.get(type);
        Time huntDuration = huntPool.huntDuration();

        Long duration = null;

        if (hunts.isActiveHunt(type)) {
            duration = hunts.getActiveHuntDurationLeft();
        } else if (huntDuration != null) {
            duration = huntDuration.toMilliseconds();
        }

        return (duration != null)
                ? DateUtils.convertToPeriod(duration)
                : "";
    }

}