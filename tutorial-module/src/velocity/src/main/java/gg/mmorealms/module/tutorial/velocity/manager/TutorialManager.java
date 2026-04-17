package gg.mmorealms.module.tutorial.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialProgress;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class TutorialManager {

	private final List<Player> tutorialPlayers = Collections.synchronizedList(new ArrayList<>());
	private final CancelableTimeTask displayTutorialTask;

	public TutorialManager() {
		this.displayTutorialTask = ScheduleUtils.runTaskTimer(this::displayTutorial, Time.seconds(1));
	}

	private void displayTutorial() {
		List<Player> toRemove = new ArrayList<>();

		// Do NOT switch to for-each loop, that operation is not thread safe on a synchronized list (for some reason)
		//noinspection ForLoopReplaceableByForEach
		for (int index = 0; index < tutorialPlayers.size(); index++) {
			Player player = tutorialPlayers.get(index);
			if (!player.isActive()) {
				toRemove.add(player);
				continue;
			}

			TutorialProgress tutorialProgress = TutorialProgress.get(player);

			if (tutorialProgress.isCompleted()) {
				toRemove.add(player);
				tutorialProgress.hideTutorial();
				return;
			}

			tutorialProgress.displayNextStep();
		}

		tutorialPlayers.removeAll(toRemove);
	}

	public void addTutorialPlayer(@Nullable Player player) {
		if (player == null) {
			return;
		}

		if (tutorialPlayers.contains(player)) {
			return;
		}

		tutorialPlayers.add(player);
	}

	public void removeTutorialPlayer(@Nullable Player player) {
		if (player == null) {
			return;
		}

		tutorialPlayers.remove(player);
	}

}
