package gg.mmorealms.module.mega_evolution.backend.fabric.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MegaEvolutionForm {
    MEGA("mega", "Mega"),
    MEGA_X("mega_x", "Mega-X"),
    MEGA_Y("mega_y", "Mega-Y"),
    MEGA_Z("mega_z", "Mega-Z");

    private final String name;
    private final String showdownName;

    MegaEvolutionForm(String name, String showdownName) {
        this.name = name;
        this.showdownName = showdownName;
    }

    @Getter
    private static final Set<String> allFormNames =
            Collections.unmodifiableSet(
                    stream()
                            .map(MegaEvolutionForm::getName)
                            .collect(Collectors.toSet())
            );

    public static Stream<MegaEvolutionForm> stream() {
        return Stream.of(values());
    }
}