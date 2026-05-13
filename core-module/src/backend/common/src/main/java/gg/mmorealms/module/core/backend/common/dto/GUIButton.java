package gg.mmorealms.module.core.backend.common.dto;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@NoArgsConstructor
public class GUIButton extends ItemBuilder {

	private Position position = new Position();

	private transient ArgLambda<ClickType> onClick = (clickType) -> {
	};

	private transient boolean placedInGUI = false;

	protected GUIButton(ItemStack baseItem, String displayName, ArrayList<String> lore, boolean hideToolTips,
	                    String skullOwner, List<CustomExecutor> customExecutors,
	                    Map<ResourceKey<Enchantment>, Integer> enchantments, PatchedDataComponentMap dataComponentMap,
	                    Map<String, Object> placeholders) {
		super(baseItem, displayName, lore, hideToolTips, skullOwner, customExecutors, enchantments, dataComponentMap,
				placeholders);
	}

	protected GUIButton(ItemStack baseItem, String displayName, ArrayList<String> lore, boolean hideToolTips,
	                    String skullOwner, List<CustomExecutor> customExecutors,
	                    Map<ResourceKey<Enchantment>, Integer> enchantments, PatchedDataComponentMap dataComponentMap,
	                    Map<String, Object> placeholders,
	                    Position position, ArgLambda<ClickType> onClick, boolean placedInGUI) {
		super(baseItem, displayName, lore, hideToolTips, skullOwner, customExecutors, enchantments, dataComponentMap,
				placeholders);
		this.position = position;
		this.onClick = onClick;
		this.placeholders = placeholders;
		this.placedInGUI = placedInGUI;
	}

	protected GUIButton(ItemStack base) {
		super(base);
	}

	public static GUIButton of(ItemStack base) {
		return new GUIButton(base);
	}

	public static GUIButton of() {
		return of(ItemStack.EMPTY);
	}

	public static GUIButton of(Item base) {
		return of(new ItemStack(base));
	}

	public static GUIButton of(ItemBuilder itemBuilder) {
		return new GUIButton(
				itemBuilder.getBaseItem(),
				itemBuilder.getDisplayName(),
				itemBuilder.getLore(),
				itemBuilder.isHideToolTips(),
				itemBuilder.getSkullOwner(),
				itemBuilder.getCustomExecutors(),
				itemBuilder.getEnchantments(),
				itemBuilder.getDataComponentMap(),
				itemBuilder.getPlaceholders()
		);
	}

	public static GUIButton empty() {
		ItemStack output = new ItemStack(Items.PAPER, 1);
		output.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1000));

		return GUIButton.of(output)
				.name("");
	}


	public GUIButton copy() {
		return new GUIButton(
				this.baseItem.copy(),
				this.displayName,
				new ArrayList<>(this.lore),
				this.hideToolTips,
				this.skullOwner,
				new ArrayList<>(this.customExecutors),
				new HashMap<>(this.enchantments),
				this.dataComponentMap.copy(),
				new HashMap<>(this.placeholders),
				this.position.copy(),
				this.onClick,
				this.placedInGUI
		);
	}

	public GUIButton markAsPlacedInGUI() {
		this.placedInGUI = true;
		return this;
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
		return position(new Position(slots));
	}

	public GUIButton position(Position position) {
		if (this.placedInGUI) {
			Logger.error("Attempted to update position of an already placed GUIButton. This is not allowed.");
			Logger.error(Thread.currentThread().getStackTrace());
			return null;
		}

		this.position = position;
		return this;
	}

	public GUIButton display(ItemStack itemStack, boolean hideLore) {
		return (GUIButton) super.display(itemStack, hideLore);
	}

	public GUIButton display(Item item, boolean hideLore) {
		return (GUIButton) super.display(new ItemStack(item), hideLore);
	}

	public GUIButton display(Item item) {
		return (GUIButton) super.display(new ItemStack(item));
	}

	public GUIButton display(ItemStack itemStack) {
		return (GUIButton) super.display(itemStack);
	}

	@Override
	public GUIButton count(int count) {
		return (GUIButton) super.count(count);
	}

	public GUIButton onClick(Lambda onClick) {
		return onClick(clickType -> onClick.run());
	}

	public GUIButton onClick(ArgLambda<ClickType> onClick) {
		this.onClick = onClick;
		return this;
	}

	@Deprecated(forRemoval = true)
	public GUIButton displayName(MessageBuilder displayName) {
		return name(displayName);
	}

	@Deprecated(forRemoval = true)
	public GUIButton displayName(String displayName) {
		return name(displayName);
	}

	@Override
	public GUIButton name(String name) {
		return (GUIButton) super.name(name);
	}

	@Override
	public GUIButton name(@NotNull MessageBuilder titleBuilder) {
		return (GUIButton) super.name(titleBuilder.parse());
	}

	@Override
	public GUIButton skullOwner(String skullOwner) {
		return (GUIButton) super.skullOwner(skullOwner);
	}

	@Override
	public GUIButton lore(@NotNull MessageBuilderList lore) {
		return (GUIButton) super.lore(lore);
	}

	@Override
	public GUIButton lore(@NotNull Collection<String> lore) {
		return (GUIButton) super.lore(lore);
	}

	@Override
	public GUIButton placeholders(Map<String, Object> placeholders) {
		return (GUIButton) super.placeholders(placeholders);
	}

	@Override
	public GUIButton placeholder(String key, Object value) {
		return (GUIButton) super.placeholder(key, value);
	}


	@Override
	public GUIButton addLore(@NotNull MessageBuilder line) {
		return (GUIButton) super.addLore(line);
	}

	@Override
	public GUIButton addLore(@NotNull String line) {
		return (GUIButton) super.addLore(line);
	}

	@Override
	public GUIButton addLore(@NotNull MessageBuilderList lore) {
		return (GUIButton) super.addLore(lore);
	}

	@Override
	public GUIButton addLore(@NotNull Collection<String> lore) {
		return (GUIButton) super.addLore(lore);
	}

	@Override
	public GUIButton addLore(@NotNull MessageBuilderList lore, int index) {
		return (GUIButton) super.addLore(lore, index);
	}

	@Override
	public GUIButton lore(@NotNull String... lore) {
		return (GUIButton) super.lore(lore);
	}

	@Override
	public GUIButton addLore(@NotNull Collection<String> lore, int index) {
		return (GUIButton) super.addLore(lore, index);
	}

	public record Position(List<Integer> slots) {
		public Position() {
			this(new ArrayList<>());
		}

		public Position copy() {
			if (this.slots == null) {
				return new Position(new ArrayList<>());
			}

			return new Position(new ArrayList<>(this.slots));
		}
	}

}
