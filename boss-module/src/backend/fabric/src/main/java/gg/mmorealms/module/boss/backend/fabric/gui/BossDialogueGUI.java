package gg.mmorealms.module.boss.backend.fabric.gui;

import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket;
import com.cobblemon.mod.common.net.serverhandling.ChallengeHandler;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.mixin.BossChallengeHandlerMixin;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Map;

public class BossDialogueGUI extends GUI {

	private static final Map<BossTier, String> TIER_GLYPHS = Map.of(
			BossTier.COMMON,     "\uF280",
			BossTier.UNCOMMON,   "\uF281",
			BossTier.RARE,       "\uF282",
			BossTier.ULTRA_RARE, "\uF283",
			BossTier.LEGENDARY,  "\uF284",
			BossTier.MEGA,       "\uF285",
			BossTier.MYTHICAL,   "\uF286"
	);

	private static final String FONT = "<font:mmorealms:boss_dialogue>";
	private static final String FONT_END = "</font>";

	private final BossTier tier;
	private final String species;
	private final int level;
	private final List<String> dialogueLines;
	private final BattleChallengePacket packet;

	public BossDialogueGUI(User user, BossTier tier, String species, int level,
	                        List<String> dialogueLines, BattleChallengePacket packet) {
		super(user, new GUISettings().chestSize(6));
		this.tier = tier;
		this.species = species;
		this.level = level;
		this.dialogueLines = dialogueLines;
		this.packet = packet;
	}

	@Override
	public String getTitleString() {
		return TIER_GLYPHS.getOrDefault(tier, "");
	}

	@Override
	protected void draw() {
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

		// Invisible click targets over the painted art on the bottom row: Battle (cols 2-3), Leave (cols 6-7).
		setButton(GUIButton.empty().position(5, 2)).onClick(this::battle);
		setButton(GUIButton.empty().position(5, 3)).onClick(this::battle);
		setButton(GUIButton.empty().position(5, 6)).onClick(this::close);
		setButton(GUIButton.empty().position(5, 7)).onClick(this::close);
	}

	private void battle() {
		playBattleFlare();
		close();
		ServerPlayer player = user.getPlayer();
		MinecraftServer server = BossFabricModule.instance().getServer();
		if (player == null || server == null) {
			return;
		}
		// Re-dispatch the original challenge through Cobblemon's own handler so the real battle pipeline runs.
		// CONFIRMED tells our ChallengeHandler mixin to let this one through instead of re-opening the dialogue.
		BossFabricModule.instance().runOnMain(() -> {
			BossChallengeHandlerMixin.CONFIRMED.add(player.getUUID());
			ChallengeHandler.INSTANCE.handle(packet, server, player);
			// Delay one beat so the battle HUD is fully visible before the title packet lands.
			ScheduleUtils.runTaskLater(
					() -> BossFabricModule.instance().runOnMain(
							() -> BossFabricModule.instance().getBossManager().sendBattleCry(player, tier, species)
					),
					Time.milliseconds(150)
			);
		});
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
