package gg.mmorealms.module.essentials.backend.common.dto;

import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.dto.Range;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ItemGroup {

	public List<Item> items = new ArrayList<>();

	public Item getRandom() {
		return RandomUtils.getRandom(items);
	}

	@AllArgsConstructor
	@Getter
	@NoArgsConstructor
	public static class Item {
		public String itemID;
		public Range range;

		public ItemStack getItemStack() {
			return ItemBuilder.of()
					.display(itemID, RandomUtils.getRandom(range))
					.build();
		}
	}

}
