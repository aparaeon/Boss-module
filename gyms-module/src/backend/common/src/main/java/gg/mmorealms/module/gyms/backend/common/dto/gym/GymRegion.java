package gg.mmorealms.module.gyms.backend.common.dto.gym;

import com.google.gson.JsonElement;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.gyms.backend.common.dto.database.GymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class GymRegion {

	private final String name;
	private final String id;
	private final List<String> requiredGyms;

	private final JsonElement displayItem;

	private final List<Gym> gyms;

	public GymRegion(String name, String id, List<String> requiredGyms,
	                 List<Gym> gyms, ItemStack displayItem, CustomModelData customModelData) {
		this.name = name;
		this.id = id;
		this.requiredGyms = requiredGyms;
		this.gyms = gyms;
		if (customModelData != null) {
			displayItem.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
		}

		this.displayItem = CodecUtils.serialize(ItemStack.CODEC, displayItem);
	}

	public GymRegion(String name, String id, List<String> requiredGyms, List<Gym> gyms, ItemStack displayItem) {
		this(name, id, requiredGyms, gyms, displayItem, null);
	}

	public GymRegion(String name, String id, List<Gym> gyms, ItemStack displayItem) {
		this(name, id, null, gyms, displayItem, null);
	}

	public boolean requiredGymsCheck(UUID playerUuid) {
		if (this.getRequiredGyms() == null) {
			return true;
		}
		for (String requiredGym : requiredGyms) {
			IUserGymRecord userGymRecord = IUserGymRecord.get(playerUuid);
			GymRecord gymRecord = userGymRecord.getGymRecord(requiredGym);
			if (gymRecord == null || gymRecord.wins() < 1) {
				return false;
			}
		}

		return true;
	}

	public ItemStack getDisplayItem() {
		return CodecUtils.deserialize(ItemStack.CODEC, displayItem, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}
}