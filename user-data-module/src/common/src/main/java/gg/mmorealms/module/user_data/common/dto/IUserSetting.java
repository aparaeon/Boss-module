package gg.mmorealms.module.user_data.common.dto;

import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import gg.mmorealms.loader.common.manager.database.annotation.InterfaceDeserializationStrategy;
import gg.mmorealms.loader.common.manager.database.enums.InterfaceDeserializationStrategyType;

@InterfaceDeserializationStrategy(
	type = InterfaceDeserializationStrategyType.DISCARD_ON_UNKNOWN
)
public interface IUserSetting<Player> extends ISerializable {

	void apply(Player player);

	void cleanup(Player player);

}
