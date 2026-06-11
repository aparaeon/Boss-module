package gg.mmorealms.module.boss.backend.fabric.config;

import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BossReward implements IWeighted {

	private double weight;
	private Range quantity;
	private MessageBuilderList rewardCommands;
	/** Label in the winner's reward summary. Null = best-effort parsed from the first reward command. */
	private @Nullable String displayName;

	@Override
	public double getWeight() {
		return weight;
	}
}
