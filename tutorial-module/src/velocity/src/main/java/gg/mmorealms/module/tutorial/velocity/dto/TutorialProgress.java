package gg.mmorealms.module.tutorial.velocity.dto;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.tutorial.velocity.TutorialVelocityModule;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "tutorial_progress")
@Getter
@NoArgsConstructor
public class TutorialProgress implements IDatabaseEntry<UUID> {

	@Id
	private UUID uuid;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<TutorialStep> completedSteps = new ArrayList<>();

	private boolean skipped;

	private transient BossBar bossBar = null;

	public TutorialProgress(UUID uuid) {
		this.uuid = uuid;
		this.getLoader().cache(uuid, this);
	}

	public static @NotNull TutorialProgress get(Player player) {
		TutorialProgress result = TutorialVelocityModule.instance().getTutorialProgressLoader().getByIdentifier(player.getUniqueId());

		if (result == null) {
			result = new TutorialProgress(player.getUniqueId());
		}

		return result;
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return TutorialVelocityModule.instance().getTutorialProgressLoader();
	}

	public boolean isCompleted() {
		return completedSteps.size() == TutorialStep.values().length || skipped;
	}

	public void displayNextStep() {
		Player player = this.getPlayer();

		if (player == null) {
			return;
		}

		TutorialStep nextStep = getCurrentStep();

		showBossBar(nextStep);

		if (nextStep == null) {
			return;
		}

		for (int i = 0; i < 100; i++) {
			player.sendMessage(TutorialVelocityModule.instance().getMiniMessageManager().parse("\n"));
		}

		for (Component component : TutorialVelocityModule.instance().getMiniMessageManager().parse(nextStep.getDescription().longDescription)) {
			player.sendMessage(component);
		}

		for (Component component : TutorialVelocityModule.instance().getMiniMessageManager().parse(TutorialVelocityModule.instance().getConfig().tutorialFooter)) {
			player.sendMessage(component);
		}
	}

	public void hideTutorial() {
		Player player = this.getPlayer();

		if (player == null) {
			return;
		}

		if (bossBar != null) {
			player.hideBossBar(bossBar);
			bossBar = null;
		}

		for (int i = 0; i < 100; i++) {
			player.sendMessage(TutorialVelocityModule.instance().getMiniMessageManager().parse("\n"));
		}
	}

	private void showBossBar(@Nullable TutorialStep nextStep) {
		Player player = this.getPlayer();

		if (player == null) {
			return;
		}

		if (bossBar != null) {
			player.hideBossBar(bossBar);
		}

		if (nextStep == null) {
			return;
		}

		int currentStep = completedSteps.size() + 1;
		int totalSteps = TutorialStep.values().length;

		Component message = TutorialVelocityModule.instance().getMiniMessageManager().parse(
				new MessageBuilder("Tutorial - {current_step}/{total_steps} - {description}")
						.parse("current_step", String.valueOf(currentStep))
						.parse("total_steps", String.valueOf(totalSteps))
						.parse("description", nextStep.getDescription().shortDescription)
						.parse()
		);

		this.bossBar = BossBar.bossBar(
				message,
				1.0f,
				BossBar.Color.GREEN,
				BossBar.Overlay.PROGRESS
		);

		player.showBossBar(bossBar);
		player.sendActionBar(message);
	}

	private @Nullable TutorialStep getCurrentStep() {
		for (TutorialStep step : TutorialStep.values()) {
			if (!completedSteps.contains(step)) {
				return step;
			}
		}

		return null;
	}

	public void attemptToFinish(TutorialStep step) {
		Player player = this.getPlayer();
		TutorialStep currentStep = getCurrentStep();

		if (player == null || currentStep == null || !currentStep.equals(step)) {
			return;
		}

		completedSteps.add(step);

		User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

		WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(
				Sounds.UI_TOAST_CHALLENGE_COMPLETE,
				SoundCategory.PLAYER,
				Vector3i.zero(),
				2500F,
				1F
		);

		user.sendPacket(packet);

	}

	public @Nullable Player getPlayer() {
		return TutorialVelocityModule.instance().getProxy().getPlayer(uuid).orElse(null);
	}

	public void reset() {
		this.completedSteps.clear();
		this.skipped = false;

		TutorialVelocityModule.instance().getTutorialManager().addTutorialPlayer(this.getPlayer());
		this.displayNextStep();
	}

	public void skip() {
		this.skipped = true;

		TutorialVelocityModule.instance().getTutorialManager().removeTutorialPlayer(this.getPlayer());
		this.hideTutorial();
	}
}
