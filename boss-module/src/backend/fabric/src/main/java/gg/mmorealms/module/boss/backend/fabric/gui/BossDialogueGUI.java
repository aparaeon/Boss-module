package gg.mmorealms.module.boss.backend.fabric.gui;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.mixin.BossPokemonDialogueMixin;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;

import java.util.List;
import java.util.Map;

public class BossDialogueGUI extends GUI {

	private static final Map<BossTier, String> TIER_GLYPHS = Map.of(
			BossTier.COMMON,     "",
			BossTier.UNCOMMON,   "",
			BossTier.RARE,       "",
			BossTier.ULTRA_RARE, "",
			BossTier.LEGENDARY,  "",
			BossTier.MEGA,       "",
			BossTier.MYTHICAL,   ""
	);

	private static final String FONT = "<font:mmorealms:boss_dialogue>";
	private static final String FONT_END = "</font>";

	private final BossTier tier;
	private final String species;
	private final int level;
	private final List<String> dialogueLines;
	private final PokemonEntity entity;
	private final InteractionHand hand;

	public BossDialogueGUI(User user, BossTier tier, String species, int level,
	                        List<String> dialogueLines, PokemonEntity entity, InteractionHand hand) {
		super(user, new GUISettings().chestSize(6));
		this.tier = tier;
		this.species = species.isEmpty() ? species : Character.toUpperCase(species.charAt(0)) + species.substring(1);
		this.level = level;
		this.dialogueLines = dialogueLines;
		this.entity = entity;
		this.hand = hand;
	}

	@Override
	public String getTitleString() {
		return "" + TIER_GLYPHS.getOrDefault(tier, "") + "豈";
	}

	@Override
	protected void draw() {
		BossConfig config = BossFabricModule.instance().getConfig();

		boolean tight = dialogueLines.size() >= 3;
		int dialogueStartRow = tight ? 2 : 3;

		setButton(GUIButton.empty()
				.position(1, 4)
				.name(FONT + "<white>" + species + " Lv." + level + FONT_END));

		for (int i = 0; i < Math.min(dialogueLines.size(), 3); i++) {
			setButton(GUIButton.empty()
					.position(dialogueStartRow + i, 1)
					.name(FONT + "<white>" + dialogueLines.get(i) + FONT_END));
		}

		setButton(config.dialogueGUI.battleButton).onClick(this::battle);
		setButton(config.dialogueGUI.leaveButton).onClick(this::close);
	}

	private void battle() {
		playBattleFlare();
		close();
		BossPokemonDialogueMixin.BATTLE_TRIGGERED.add(user.getUUID());
		entity.mobInteract(user.getPlayer(), hand);
	}

	private void playBattleFlare() {
		if (user.getPlayer() == null) {
			return;
		}
		var player = user.getPlayer();
		player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.9F);
		player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 0.35F, 1.15F);
	}
}
