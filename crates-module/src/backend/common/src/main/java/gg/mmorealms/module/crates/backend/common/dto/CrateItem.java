package gg.mmorealms.module.crates.backend.common.dto;

import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CrateItem implements IWeighted {

	private GUIButton displayItem;
	private MessageBuilderList rewardCommands;
	private @Setter double chance;

	@Override
	public double getWeight() {
		return chance;
	}
}
