package gg.mmorealms.module.hunts.backend.common.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.hunts.backend.common.dto.HuntPool;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.gui.AcceptButton;
import gg.mmorealms.module.hunts.backend.common.gui.DenyButton;
import gg.mmorealms.module.hunts.backend.common.gui.HuntBackground;
import gg.mmorealms.module.hunts.backend.common.gui.HuntTarget;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuntsConfig {

    public Time huntsRefreshInterval = Time.seconds(5);

    public Map<HuntType, HuntPool> huntPools = new HashMap<>() {{
        put(HuntType.BEGINNER, new HuntPool(
                Time.hours(2),
                Time.minutes(15),
                List.of(
                        "pidgey",
                        "rattata",
                        "weedle",
                        "caterpie",
                        "spearow",
                        "bellsprout",
                        "oddish",
                        "magikarp",
                        "geodude",
                        "ekans"
                ),
                false,
                false,
                false,
                null
        ));

        put(HuntType.CHALLENGING, new HuntPool(
                Time.hours(6),
                Time.minutes(15),
                List.of(
                        "charmander",
                        "squirtle",
                        "bulbasaur",
                        "pikachu",
                        "abra",
                        "lapras",
                        "dratini",
                        "growlithe",
                        "vulpix",
                        "snorlax"
                ),
                true,
                true,
                false,
                Range.of(10, 20)
        ));

        put(HuntType.INSANE, new HuntPool(
                null,
                Time.minutes(15),
                List.of(
                        "ditto",
                        "snorlax"
                ),
                false,
                false,
                true,
                null
        ));
    }};

    public HuntsGUI gui = new HuntsGUI();

    public static class HuntsGUI {
        public String title = "<red><b>HUNTS";

        public Map<HuntType, HuntRow> rows = new HashMap<>() {{
            put(HuntType.BEGINNER, new HuntRow(
                    List.of("/balance add {user} 1000 PokeCoins"),
                    new HuntTarget(
                            new GUIButton()
                                    .display(Items.NETHER_STAR)
                                    .displayName("<white>Hunt on ???")
                                    .lore(List.of(
                                            "<gray>- <b><white>Difficulty: <aqua>Beginner",
                                            "<gray>- <b><white>Duration:</b> {duration}",
                                            "<gray>- <b><gradient:gold:yellow:gold>Reward:</gradient></b> <aqua>1000 <gradient:#FF0000:white><bold>PokeCoins"
                                    ))
                                    .position(0, 0),

                            new GUIButton()
                                    .displayName("Hunt on {target}")
                                    .lore(List.of(
                                            "<gray>- <b><white>Difficulty: <aqua>Beginner",
                                            "<gray>- <b><white>Duration:</b> {duration}",
                                            "<gray>- <b><gradient:gold:yellow:gold>Reward:</gradient></b> <aqua>1000 <gradient:#FF0000:white><bold>PokeCoins"
                                    ))
                                    .position(0, 0),

                            new GUIButton()
                                    .display(Items.BARRIER)
                                    .displayName("<gray>New hunt in {huntDenyCooldown}")
                                    .position(0, 0)
                    ),
                    new DenyButton(
                            new GUIButton()
                                    .display(Items.RED_STAINED_GLASS)
                                    .displayName("<red>Deny ✗")
                                    .position(0, 7),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS)
                                    .displayName("<gray>Deny ✗")
                                    .position(0, 7)
                    ),
                    new AcceptButton(
                            new GUIButton()
                                    .display(Items.GREEN_STAINED_GLASS)
                                    .displayName("<green>Accept ✔")
                                    .position(0, 8),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS)
                                    .displayName("<gray>Accept ✔")
                                    .position(0, 8)
                    ),
                    new HuntBackground(
                            new GUIButton()
                                    .display(Items.CYAN_STAINED_GLASS_PANE)
                                    .displayName("")
                                    .position(0, 1, 6, 1),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS_PANE)
                                    .displayName("")
                                    .position(0, 1, 6, 1)
                    )
            ));

            put(HuntType.CHALLENGING, new HuntRow(
                    List.of("/balance add {user} 10000 PokeCoins"),
                    new HuntTarget(
                            new GUIButton()
                                    .display(Items.NETHER_STAR)
                                    .displayName("<white>Hunt on ???")
                                    .lore(List.of(
                                            "<gray>- <b><white>Difficulty: <light_purple>Challenging",
                                            "<gray>- <b><white>Duration:</b> {duration}",
                                            "<gray>- <white>Gender: ???",
                                            "<gray>- <white>Nature: ???",
                                            "<gray>- <white>Min. Average IVs: ???",
                                            "<gray>- <b><gradient:gold:yellow:gold>Reward:</gradient></b> <aqua>10000 <gradient:#FF0000:white><bold>PokeCoins"
                                    ))
                                    .position(1, 0),

                            new GUIButton()
                                    .displayName("Hunt on {target}")
                                    .lore(List.of(
                                            "<gray>- <b><white>Difficulty: <light_purple>Challenging",
                                            "<gray>- <b><white>Duration:</b> {duration}",
                                            "<gray>- <white>Gender: {gender}",
                                            "<gray>- <white>Nature: {nature}",
                                            "<gray>- <white>Min. Average IVs: {averageIVs}",
                                            "<gray>- <b><gradient:gold:yellow:gold>Reward:</gradient></b> <aqua>10000 <gradient:#FF0000:white><bold>PokeCoins"
                                    ))
                                    .position(1, 0),

                            new GUIButton()
                                    .display(Items.BARRIER)
                                    .displayName("<gray>New hunt in {huntDenyCooldown}")
                                    .position(1, 0)
                    ),
                    new DenyButton(
                            new GUIButton()
                                    .display(Items.RED_STAINED_GLASS)
                                    .displayName("<red>Deny ✗")
                                    .position(1, 7),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS)
                                    .displayName("<gray>Deny ✗")
                                    .position(1, 7)
                    ),
                    new AcceptButton(
                            new GUIButton()
                                    .display(Items.GREEN_STAINED_GLASS)
                                    .displayName("<green>Accept ✔")
                                    .position(1, 8),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS)
                                    .displayName("<gray>Accept ✔")
                                    .position(1, 8)
                    ),
                    new HuntBackground(
                            new GUIButton()
                                    .display(Items.PURPLE_STAINED_GLASS_PANE)
                                    .displayName("")
                                    .position(1, 1, 6, 1),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS_PANE)
                                    .displayName("")
                                    .position(1, 1, 6, 1)
                    )
            ));

            put(HuntType.INSANE, new HuntRow(
                    List.of("/balance add {user} 30000 PokeCoins"),
                    new HuntTarget(
                            new GUIButton()
                                    .display(Items.NETHER_STAR)
                                    .displayName("<white>Hunt on ???")
                                    .lore(List.of(
                                            "<gray>- <b><white>Difficulty: <red>Insane",
                                            "<gray>- <b><gradient:gold:yellow:gold>Reward:</gradient></b> <aqua>30000 <gradient:#FF0000:white><bold>PokeCoins"
                                    ))
                                    .position(2, 0),

                            new GUIButton()
                                    .displayName("Hunt on <gradient:gold:yellow:gold>Shiny {target}</gradient>")
                                    .lore(List.of(
                                            "<gray>- <b><white>Difficulty: <red>Insane",
                                            "<gray>- <b><gradient:gold:yellow:gold>Reward:</gradient></b> <aqua>30000 <gradient:#FF0000:white><bold>PokeCoins"
                                    ))
                                    .position(2, 0),

                            new GUIButton()
                                    .display(Items.BARRIER)
                                    .displayName("<gray>New hunt in {huntDenyCooldown}")
                                    .position(2, 0)
                    ),
                    new DenyButton(
                            new GUIButton()
                                    .display(Items.RED_STAINED_GLASS)
                                    .displayName("<red>Deny ✗")
                                    .position(2, 7),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS)
                                    .displayName("<gray>Deny ✗")
                                    .position(2, 7)
                    ),
                    new AcceptButton(
                            new GUIButton()
                                    .display(Items.GREEN_STAINED_GLASS)
                                    .displayName("<green>Accept ✔")
                                    .position(2, 8),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS)
                                    .displayName("<gray>Accept ✔")
                                    .position(2, 8)
                    ),
                    new HuntBackground(
                            new GUIButton()
                                    .display(Items.RED_STAINED_GLASS_PANE)
                                    .displayName("")
                                    .position(2, 1, 6, 1),

                            new GUIButton()
                                    .display(Items.GRAY_STAINED_GLASS_PANE)
                                    .displayName("")
                                    .position(2, 1, 6, 1)
                    )
            ));
        }};
    }

    public Lang lang = new Lang();

    public static class Lang {
        public MessageBuilder huntAccepted = new MessageBuilder("<yellow>You have accepted hunt on {target}!");
        public MessageBuilder huntCompleted = new MessageBuilder("<green>You have successfully completed hunt!");
        public MessageBuilder huntFailed = new MessageBuilder("<red>You haven't completed hunt in time");
        public MessageBuilder huntGenerated = new MessageBuilder("<yellow>New {type} hunt available");
        public String huntAcceptingError ="<red>Error accepting hunt, no such speciesName";
    }
}
