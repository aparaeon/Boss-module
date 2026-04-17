package gg.mmorealms.module.mega_evolution.backend.fabric.dto;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import gg.mmorealms.module.core.common.utils.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MegaEvolution {
    ABOMASNOW("abomasnow", "abomasite"),
    ABSOL("absol", "absolite"),
    ABSOL_Z("absol", "absolite_z", MegaEvolutionForm.MEGA_Z, true),
    AERODACTYL("aerodactyl", "aerodactylite"),
    AGGRON("aggron", "aggronite", true),
    ALAKAZAM("alakazam", "alakazite"),
    ALTARIA("altaria", "altarianite"),
    AMPHAROS("ampharos", "ampharosite"),
    AUDINO("audino", "audinite"),
    BANETTE("banette", "banettite"),
    BARBARACLE("barbaracle", "barbaracite", true),
    BAXCALIBUR("baxcalibur", "baxcalibrite", true),
    BEEDRILL("beedrill", "beedrillite"),
    BLASTOISE("blastoise", "blastoisinite"),
    BLAZIKEN("blaziken", "blazikenite"),
    CAMERUPT("camerupt", "cameruptite"),
    CHANDELURE("chandelure", "chandelurite", true),
    CHARIZARD_X("charizard", "charizardite_x", MegaEvolutionForm.MEGA_X),
    CHARIZARD_Y("charizard", "charizardite_y", MegaEvolutionForm.MEGA_Y),
    CHESNAUGHT("chesnaught", "chesnaughtite", true),
    CHIMECHO("chimecho", "chimechite", true),
    CLEFABLE("clefable", "clefablite", true),
    CRABOMINABLE("crabominable", "crabominite", true),
    DARKRAI("darkrai", "darkranite", true),
    DELPHOX("delphox", "delphoxite", true),
    DIANCIE("diancie", "diancite"),
    DRAGALGE("dragalge", "dragalgite", true),
    DRAGONITE("dragonite", "dragoninite", true),
    DRAMPA("drampa", "drampanite", true),
    EELEKTROSS("eelektross", "eelektrossite", true),
    EMBOAR("emboar", "emboarite", true),
    EXCADRILL("excadrill", "excadrite", true),
    FALINKS("falinks", "falinksite", true),
    FERALIGATR("feraligatr", "feraligite", true),
    FLOETTE("floette", "floettite", true),
    FROSLASS("froslass", "froslassite", true),
    GALLADE("gallade", "galladite"),
    GARCHOMP("garchomp", "garchompite"),
    GARCHOMP_Z("garchomp", "garchompite_z", MegaEvolutionForm.MEGA_Z, true),
    GARDEVOIR("gardevoir", "gardevoirite"),
    GENGAR("gengar", "gengarite"),
    GLALIE("glalie", "glalitite"),
    GLIMMORA("glimmora", "glimmoranite", true),
    GOLISOPOD("golisopod", "golisopite", true),
    GOLURK("golurk", "golurkite", true),
    GRENINJA("greninja", "greninjite", true),
    GYARADOS("gyarados", "gyaradosite"),
    HAWLUCHA("hawlucha", "hawluchanite", true),
    HEATRAN("heatran", "heatranite", true),
    HERACROSS("heracross", "heracronite"),
    HOUNDOOM("houndoom", "houndoominite"),
    KANGASKHAN("kangaskhan", "kangaskhanite"),
    LATIAS("latias", "latiasite"),
    LATIOS("latios", "latiosite"),
    LOPUNNY("lopunny", "lopunnite"),
    LUCARIO("lucario", "lucarionite"),
    LUCARIO_Z("lucario", "lucarionite_z", MegaEvolutionForm.MEGA_Z, true),
    MAGEARNA("magearna", "magearnite", true),
    MALAMAR("malamar", "malamarite", true),
    MANECTRIC("manectric", "manectite"),
    MAWILE("mawile", "mawilite"),
    MEDICHAM("medicham", "medichamite"),
    MEGANIUM("meganium", "meganiumite", true),
    MEOWSTIC("meowstic", "meowsticite", true),
    METAGROSS("metagross", "metagrossite"),
    MEWTWO_X("mewtwo", "mewtwonite_x", MegaEvolutionForm.MEGA_X),
    MEWTWO_Y("mewtwo", "mewtwonite_y", MegaEvolutionForm.MEGA_Y),
    PIDGEOT("pidgeot", "pidgeotite"),
    PINSIR("pinsir", "pinsirite"),
    PYROAR("pyroar", "pyroarite", true),
    RAICHU_X("raichu", "raichunite_x", MegaEvolutionForm.MEGA_X, true),
    RAICHU_Y("raichu", "raichunite_y", MegaEvolutionForm.MEGA_Y, true),
    RAYQUAZA("rayquaza"), // No stone needed
    SABLEYE("sableye", "sablenite"),
    SALAMENCE("salamence", "salamencite"),
    SCEPTILE("sceptile", "sceptilite"),
    SCIZOR("scizor", "scizorite"),
    SCOLIPEDE("scolipede", "scolipite", true),
    SCOVILLAIN("scovillain", "scovillainite", true),
    SCRAFTY("scrafty", "scraftinite", true),
    SHARPEDO("sharpedo", "sharpedonite"),
    SKARMORY("skarmory", "skarmorite", true),
    SLOWBRO("slowbro", "slowbronite"),
    STARAPTOR("staraptor", "staraptite", true),
    STARMIE("starmie", "starminite", true),
    STEELIX("steelix", "steelixite"),
    SWAMPERT("swampert", "swampertite"),
    TATSUGIRI("tatsugiri", "tatsugirinite", true),
    TYRANITAR("tyranitar", "tyranitarite"),
    VENUSAUR("venusaur", "venusaurite"),
    VICTREEBEL("victreebel", "victreebelite", true),
    ZERAORA("zeraora", "zeraorite", true),
    ZYGARDE("zygarde", "zygardite", true);

    private final String speciesName;
    @Nullable
    private final MegaEvolutionGem gem;
    private final MegaEvolutionForm form;
    private final boolean isShowdownInjected;

    private static final Map<String, List<MegaEvolution>> BY_SPECIES =
            Collections.unmodifiableMap(stream()
                    .collect(Collectors.groupingBy(MegaEvolution::getSpeciesName)));

    MegaEvolution(String speciesName) {
        this(speciesName, (String) null, MegaEvolutionForm.MEGA, false);
    }

    MegaEvolution(String speciesName, String gemName) {
        this(speciesName, new MegaEvolutionGem(gemName), MegaEvolutionForm.MEGA, false);
    }

    MegaEvolution(String speciesName, String gemName, boolean isShowdownInjected) {
        this(speciesName, new MegaEvolutionGem(gemName), MegaEvolutionForm.MEGA, isShowdownInjected);
    }

    MegaEvolution(String speciesName, String gemName, MegaEvolutionForm form) {
        this(speciesName, new MegaEvolutionGem(gemName), form, false);
    }

    MegaEvolution(String speciesName, String gemName, MegaEvolutionForm form, boolean isShowdownInjected) {
        this(speciesName, gemName != null ? new MegaEvolutionGem(gemName) : null, form, isShowdownInjected);
    }

    MegaEvolution(String speciesName, @Nullable MegaEvolutionGem gem, MegaEvolutionForm form, boolean isShowdownInjected) {
        this.speciesName = speciesName;
        this.gem = gem;
        this.form = form;
        this.isShowdownInjected = isShowdownInjected;
    }

    public boolean hasGem() {
        return gem != null;
    }

    public String getGemName() {
        return hasGem() ? gem.getGemName() : "";
    }

    public String getStoneOreName() {
        return hasGem() ? gem.getStoneOreName() : "";
    }

    public String getDeepslateOreName() {
        return hasGem() ? gem.getDeepslateOreName() : "";
    }

    public String getGemDisplayName() {
        return hasGem() ? gem.getGemDisplayName() : "";
    }

    public String getStoneOreDisplayName() {
        return hasGem() ? gem.getStoneOreDisplayName() : "";
    }

    public String getDeepslateOreDisplayName() {
        return hasGem() ? gem.getDeepslateOreDisplayName() : "";
    }

    public String getGemShowdownName() {
        return hasGem() ? StringUtils.toTitleCase(gem.getGemName()) + "-" + form.getShowdownName() : "";
    }

    public String getMegaAspect() {
        return form.getName();
    }

    public static Set<String> getMegaAspects() {
        return MegaEvolutionForm.getAllFormNames();
    }

    public static List<MegaEvolution> fromPokemon(Pokemon pokemon) {
        return fromSpecies(pokemon.getSpecies());
    }

    public static List<MegaEvolution> fromSpecies(Species species) {
        return BY_SPECIES.getOrDefault(species.toString(), List.of());
    }

    public static Stream<MegaEvolution> stream() {
        return Stream.of(MegaEvolution.values());
    }

    public static Stream<MegaEvolution> streamWithGems() {
        return stream().filter(MegaEvolution::hasGem);
    }

    public static <T> Map<MegaEvolution, T> linkedMapWithGems(Function<MegaEvolution, T> mapper) {
        return PolymerRegistryUtils.buildLinkedMap(streamWithGems(), mapper);
    }

}