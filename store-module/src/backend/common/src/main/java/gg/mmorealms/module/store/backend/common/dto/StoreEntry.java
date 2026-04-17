package gg.mmorealms.module.store.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class StoreEntry  {

	protected String id;
	protected String name;
	protected String image;
	protected Price price;
	protected MessageBuilderList commands;

	protected transient GUIButton guiButton;

	public StoreEntry(String id, String name, String image, Price price, MessageBuilderList commands) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.price = price;
		this.commands = commands;
	}

	public abstract void bake();

}
