package gg.mmorealms.module.tutorial.velocity.files;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialStep;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialStepDescription;

import java.util.HashMap;
import java.util.List;

public class TutorialConfig {

	public MessageBuilderList tutorialFooter = new MessageBuilderList(List.of(
			"",
			"<dark_gray>If you want to skip the tutorial, you can type <green>/tutorial skip<reset>."
	));

	public HashMap<TutorialStep, TutorialStepDescription> tutorialSteps = new HashMap<>() {{
		put(TutorialStep.CLAIM_STARTER, new TutorialStepDescription(
				"Press <aqua>M <reset>to chose your starter",
				new MessageBuilderList(List.of(
						"<gold><b>Welcome to <aqua><b>MMO Realms - Cobblemon<reset>",
						"",
						"<gray>Let's start your journey by choosing your starter Pokémon.",
						"<gray>Press <aqua>M <gray>to select your starter Pokémon."
				))
		));
		put(TutorialStep.RTP, new TutorialStepDescription(
				"Use <aqua>/rtp <reset>to to teleport randomly to a wild",
				new MessageBuilderList(List.of(
						"<gray>You can use <green>/rtp<gray> to teleport to a random wild area.",
						"<gray>This will allow you to find wild Pokémon and battle them as well as find resources and items.",
						"<gray>Wilds do reset every week so make sure to check back often!",
						"<gray><red>DO NOT BUILD YOUR BASE IN THE WILD!<gray> It will be removed after the reset."
				))
		));
		put(TutorialStep.SEND_POKEMON, new TutorialStepDescription(
				"Press <aqua>R <reset>to send your Pokémon",
				new MessageBuilderList(List.of(
						"<gray>Press <aqua>R <gray>to send your Pokémon out.",
						"<gray>You can select which Pokémon to send out by using the arrow keys.",
						"<gray>You can modify your party by moving pokemons in and out of your PC",
						"<gray>By sending your Pokémon out, you can fight and catch other Pokemons."
				))
		));
		put(TutorialStep.WARP_HEAL, new TutorialStepDescription(
				"Use <aqua>/warp heal <gray>to heal your Pokémon",
				new MessageBuilderList(List.of(
						"<gray>Use <aqua>/warp heal<gray> to teleport to the healing area.",
						"<gray>This will allow you to heal your Pokémon and get them ready for battle."
				))
		));
		put(TutorialStep.REALM_TP, new TutorialStepDescription(
				"Use <aqua>/realm <reset>to create your realm",
				new MessageBuilderList(List.of(
						"<gray>The realm is your personal space where you can build and store all of your items and Pokémon.",
						"<gray>To create a realm, type <aqua>/realm<gray>.",
						"<gray>You can invite other players to your realm using <aqua>/realm invite <player><gray>.",
						"<gray>For more information about realms, you can check the <gold>Wiki <gray>or <aqua>/realm help<gray>."
				))
		));
	}};

}
