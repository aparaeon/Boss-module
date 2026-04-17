package gg.mmorealms.module.crates.backend.common.dto;

import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import gg.mmorealms.module.economy.common.dto.Price;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Crate {

	public String id;
	public String name;
	public List<CrateItem> crateItems;
	public GUIButton displayItem;
	public Price price;
	public Location physicalLocation;
	public String lootLore = "";
	public transient boolean baked = false;

	public Crate(String id, String name, List<CrateItem> crateItems, GUIButton displayItem, Price price, Location physicalLocation) {
		this.id = id;
		this.name = name;
		this.crateItems = crateItems;
		this.displayItem = displayItem;
		this.price = price;
		this.physicalLocation = physicalLocation;
	}

	private void bakeLootLore() {
		int index = this.displayItem.getLore().indexOf("{loot}");

		if (index == -1) {
			return;
		}

		List<String> lore = this.displayItem.getLore();
		lore.remove(index);

		for (int i = 0; i < crateItems.size(); i++) {
			CrateItem crateItem = crateItems.get(i);
			lore.add(
					index + i,
					CratesBackendModule.instance().getConfig().crateLootLore
							.parse("name", crateItem.getDisplayItem().getDisplayName())
							.parse("chance", NumberUtils.formatNumberWithDecimalPlaces(crateItem.getChance(), 2))
							.parse()
			);
		}
	}

	public void bake() {
		if (baked) {
			return;
		}
		crateItems = new ArrayList<>(crateItems);

		crateItems.sort((o1, o2) -> -Double.compare(o1.getChance(), o2.getChance()));
		double totalChance = 0;
		for (CrateItem crateItem : crateItems) {
			totalChance += crateItem.getChance();
		}

		for (CrateItem item : crateItems) {
			double normalizedChance = item.getChance() / totalChance * 100;
			item.setChance(normalizedChance);
			List<String> lore = item.getDisplayItem().getLore();
			lore.addAll(
					CratesBackendModule.instance().getConfig().displayItemLore
							.parse("chance", NumberUtils.formatNumberWithDecimalPlaces(normalizedChance, 2))
							.parse()
			);
		}

		bakeLootLore();

		baked = true;
	}

}
