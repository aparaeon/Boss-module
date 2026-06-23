package gg.mmorealms.module.boss.backend.fabric.gui;

import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket;
import com.cobblemon.mod.common.net.serverhandling.ChallengeHandler;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.BossMixinState;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
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

	private final BossTier tier;
	private final String species;
	private final int level;
	private final BattleChallengePacket packet;

	public BossDialogueGUI(User user, BossTier tier, String species, int level,
	                        List<String> dialogueLines, BattleChallengePacket packet) {
		super(user, new GUISettings().chestSize(6));
		this.tier = tier;
		this.species = species;
		this.level = level;
		this.packet = packet;
	}

	@Override
	public void open() {
		super.open();
		ServerPlayer player = user.getPlayer();
		if (player != null) {
			BossFabricModule.instance().getBossManager().sendEncounterPopup(player, tier, species, level);
		}
	}

	@Override
	public String getTitleString() {
		return TIER_GLYPHS.getOrDefault(tier, "");
	}

	@Override
	protected void draw() {
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
		BossFabricModule.instance().runOnMain(() -> {
			BossFabricModule.instance().getBossManager().sendBattleCry(player, tier, species);
			BossMixinState.CONFIRMED.add(player.getUUID());
			ChallengeHandler.INSTANCE.handle(packet, server, player);
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
