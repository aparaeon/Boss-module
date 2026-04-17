package gg.mmorealms.module.store.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class StoreCurrency extends StoreEntry {

	public StoreCurrency(String id, String name, String image, Price price, MessageBuilderList commands) {
		super(id, name, image, price, commands);
	}

	@Override
	public void bake() {
		this.guiButton = GUIButton.empty()
				.displayName(this.name)
				.lore(StoreBackendModule.instance().getConfig().lang.currencyTemplate);
	}

}
