package gg.mmorealms.module.store.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StoreRank extends StoreEntry {

	private List<String> oneTimeRewardsList;
	private List<String> commandsList;
	private List<String> kitsList;

	private transient GUIButton guiButton;

	public StoreRank(String id, String name, String bigImage, Price price, MessageBuilderList commands,
	                 List<String> oneTimeRewardsList, List<String> commandsList, List<String> kitsList) {
		super(id, name, bigImage, price, commands);

		this.oneTimeRewardsList = oneTimeRewardsList;
		this.commandsList = commandsList;
		this.kitsList = kitsList;
	}

	@Override
	public void bake() {
		List<String> oneTimeRewardsBacked = new ArrayList<>();
		List<String> commandsBacked = new ArrayList<>();
		List<String> kitsBacked = new ArrayList<>();

		for (String entry : this.oneTimeRewardsList) {
			oneTimeRewardsBacked.add(
					StoreBackendModule.instance().getConfig().lang.rankEntryTemplate
							.parse("entry", entry)
							.parse()
			);
		}
		for (String entry : this.commandsList) {
			commandsBacked.add(
					StoreBackendModule.instance().getConfig().lang.rankEntryTemplate
							.parse("entry", entry)
							.parse()
			);
		}
		for (String entry : this.kitsList) {
			kitsBacked.add(
					StoreBackendModule.instance().getConfig().lang.rankEntryTemplate
							.parse("entry", entry)
							.parse()
			);
		}

		this.guiButton = GUIButton.empty()
				.displayName(this.name)
				.lore(
						StoreBackendModule.instance().getConfig().lang.rankTemplate
								.parse("one_time_items", oneTimeRewardsBacked)
								.parse("commands", commandsBacked)
								.parse("kits", kitsBacked)
								.parse()
				);
	}

	public String getRank() {
		return StoreBackendModule.instance().getMiniMessageManager().sanitize(this.name);
	}

}
