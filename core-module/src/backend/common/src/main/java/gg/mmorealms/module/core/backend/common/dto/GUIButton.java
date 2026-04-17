package gg.mmorealms.module.core.backend.common.dto;

import com.google.gson.annotations.Expose;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GUIButton {

	private String displayJson = "{\"id\":\"minecraft:air\",\"count\":1}";
	private String displayName = null;
	private String skullOwner = null;
	private List<String> lore = new ArrayList<>();
	private GUIPosition position = new GUIPosition(new ArrayList<>());

	private transient @Expose(deserialize = false, serialize = false) ArgLambda<ClickType> onClick = (clickType) -> {
	};
	private transient @Expose(deserialize = false, serialize = false) Map<String, Object> placeholders = new HashMap<>();
	private transient @Expose(deserialize = false, serialize = false) boolean cloned = false;
	private transient @Expose(deserialize = false, serialize = false) boolean placedInGUI = false;

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public GUIButton clone() {
		return new GUIButton(
			this.displayJson,
			this.displayName,
			this.skullOwner,
			new ArrayList<>(this.lore),
			this.position.clone(),
			(clickType) -> this.onClick.run(clickType),
			new HashMap<>(this.placeholders),
			true,
			this.placedInGUI
		);
	}

	public GUIButton markAsPlacedInGUI() {
		this.placedInGUI = true;
		return this;
	}

	public GUIButton cloneIfNotCloned() {
		if (this.cloned) {
			return this;
		}
		return this.clone();
	}

	private GUIButton executeOnObject(ArgLambda<GUIButton> executor) {
		GUIButton workingCopy = cloneIfNotCloned();
		executor.run(workingCopy);
		return workingCopy;
	}

	public GUIButton position(Integer slot) {
		return position(List.of(slot));
	}

	public GUIButton position(Integer... slots) {
		return position(List.of(slots));
	}

	public GUIButton position(int row, int column) {
		return this.position(row, column, 1, 1);
	}

	public GUIButton position(int startRow, int startColumn, int xSize, int ySize) {
		List<Integer> slots = new ArrayList<>();

		for (int row = startRow; row < startRow + ySize; row++) {
			for (int column = startColumn; column < startColumn + xSize; column++) {
				slots.add(row * 9 + column);
			}
		}

		return position(slots);
	}

	public GUIButton position(List<Integer> slots) {
		return position(new GUIPosition(slots));
	}

	public GUIButton position(GUIPosition position) {
		if (this.placedInGUI) {
			Logger.error("Attempted to update position of an already placed GUIButton. This is not allowed.");
			Logger.error(Thread.currentThread().getStackTrace());
			return null;
		}

		return this.executeOnObject(
			guiItem ->
				guiItem.position = position
		);
	}

	public GUIButton display(Item item, boolean hideLore) {
		return display(new ItemStack(item), hideLore);
	}

	public GUIButton display(Item item) {
		return display(new ItemStack(item), false);
	}

	public GUIButton display(ItemStack itemStack) {
		return display(itemStack, false);
	}

	public GUIButton display(ItemStack itemStack, boolean hideLore) {
		return executeOnObject(
			guiItem -> {
				if (hideLore) {
					itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
				}
				guiItem.displayJson = CodecUtils.serialize(ItemStack.CODEC, itemStack).toString();
			}
		);
	}

	public GUIButton display(ItemBuilder itemBuilder, boolean hideLore) {
		return display(itemBuilder.build(), hideLore);
	}

	public GUIButton display(ItemBuilder itemBuilder) {
		return display(itemBuilder.build(), false);
	}

	public GUIButton count(int count) {
		return executeOnObject(
			guiItem -> {
				ItemStack newDisplayItem = guiItem.toItemStack();
				newDisplayItem.setCount(count);
				guiItem.display(newDisplayItem, false);
			}
		);
	}

	public GUIButton onClick(Lambda onClick) {
		return onClick(clickType -> onClick.run());
	}

	public GUIButton onClick(ArgLambda<ClickType> onClick) {
		return executeOnObject(
			guiItem ->
				guiItem.onClick = onClick
		);
	}

	public GUIButton displayName(MessageBuilder displayName) {
		return displayName(displayName.parse());
	}

	public GUIButton displayName(String displayName) {
		return executeOnObject(
			guiItem ->
				guiItem.displayName = displayName
		);
	}

	public GUIButton skullOwner(String skullOwner) {
		return executeOnObject(
			guiItem ->
				guiItem.skullOwner = skullOwner
		);
	}

	public GUIButton lore(String... lore) {
		return lore(List.of(lore));
	}

	public GUIButton lore(MessageBuilderList lore) {
		return lore(lore.parse());
	}

	public GUIButton lore(List<String> lore) {
		return executeOnObject(
			guiItem ->
				guiItem.lore = lore
		);
	}

	public GUIButton placeholders(Map<String, Object> placeholders) {
		return executeOnObject(
			guiItem -> guiItem.placeholders = placeholders
		);
	}

	public GUIButton placeholder(String key, Object value) {
		return executeOnObject(
			guiItem -> guiItem.placeholders.put(key, value)
		);
	}

	public static GUIButton empty() {
		ItemStack output = new ItemStack(Items.PAPER, 1);
		output.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1000));

		return new GUIButton()
			.displayName("")
			.display(output, false);
	}

	public ItemStack toItemStack() {
		return ItemBuilder.of(this).build();
	}

	public record GUIPosition(List<Integer> slots) {
		@SuppressWarnings("MethodDoesntCallSuperMethod")
		public GUIPosition clone() {
			if (this.slots == null) {
				return new GUIPosition(new ArrayList<>());
			}

			return new GUIPosition(new ArrayList<>(this.slots));
		}
	}

}
