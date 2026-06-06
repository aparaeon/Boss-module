package gg.mmorealms.module.boss.backend.fabric.config;

import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BossReward implements IWeighted {

	private double weight;
	private Range quantity;
	private MessageBuilderList rewardCommands;

	@Override
	public double getWeight() {
		return weight;
	}
}
