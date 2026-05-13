package gg.mmorealms.module.core.backend.common.utils;

import com.mojang.authlib.properties.PropertyMap;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO find a way to have the lore be straight (not italics)
@Getter
@NoArgsConstructor
public class ItemBuilder {

	private transient ItemStack cache = null;
	private transient boolean dirty = true;

	protected ItemStack baseItem = new ItemStack(Items.AIR);
	protected String displayName = null;
	protected ArrayList<String> lore = new ArrayList<>();
	protected boolean hideToolTips = false;
	protected String skullOwner = null;

	// Unserializable fields - handled manually // TODO Maybe look into how to serialize them as well
	protected transient List<CustomExecutor> customExecutors = new ArrayList<>(); // TODO Maybe change to ArgLambdaExecutor<ItemBuilder>
	protected transient Map<ResourceKey<Enchantment>, Integer> enchantments = new HashMap<>();
	protected transient PatchedDataComponentMap dataComponentMap = new PatchedDataComponentMap(DataComponentMap.EMPTY);

	protected transient Map<String, Object> placeholders = new HashMap<>();

	public ItemBuilder(ItemStack baseItem, String displayName, ArrayList<String> lore, boolean hideToolTips,
	                   String skullOwner, List<CustomExecutor> customExecutors,
	                   Map<ResourceKey<Enchantment>, Integer> enchantments, PatchedDataComponentMap dataComponentMap,
	                   Map<String, Object> placeholders) {
		this.baseItem = baseItem;
		this.displayName = displayName;
		this.lore = lore;
		this.hideToolTips = hideToolTips;
		this.skullOwner = skullOwner;
		this.customExecutors = customExecutors;
		this.enchantments = enchantments;
		this.dataComponentMap = dataComponentMap;
		this.placeholders = placeholders;
	}

	protected ItemBuilder(ItemStack base) {
		this.baseItem = base;
	}

	public static ItemBuilder of() {
		return new ItemBuilder(ItemStack.EMPTY);
	}

	public static ItemBuilder of(ItemStack base) {
		return new ItemBuilder(base);
	}

	public ItemBuilder skullOwner(String skullOwner) {
		this.skullOwner = skullOwner;
		this.display("minecraft:player_head", 1);
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public ItemBuilder display(@NotNull String itemID, int amount) {
		Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemID));
		ItemStack itemStack = new ItemStack(item, amount);
		return display(itemStack);
	}

	public ItemBuilder display(@NotNull ItemStack display, boolean hideLore) {
		this.markDirty();
		if (hideLore) {
			display.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
		}
		this.baseItem = display;
		return this;
	}

	public ItemBuilder display(@NotNull ItemStack display) {
		return display(display, false);
	}

	@SuppressWarnings("unused")
	public ItemBuilder display(@NotNull Item item) {
		return this.display(item, 1);
	}

	public ItemBuilder display(@NotNull Item item, int count) {
		return this.display(new ItemStack(item, count));
	}

	public ItemBuilder count(int count) {
		this.baseItem.setCount(count);
		return this;
	}

	public ItemBuilder name(@NotNull MessageBuilder titleBuilder) {
		return this.name(titleBuilder.parse());
	}

	public ItemBuilder name(@Nullable String name) {
		this.markDirty();
		this.displayName = name;
		return this;
	}

	public ItemBuilder lore(@NotNull String... lore) {
		return this.lore(List.of(lore));
	}

	public ItemBuilder lore(@NotNull MessageBuilderList lore) {
		return this.lore(lore.parse());
	}

	public ItemBuilder lore(@NotNull Collection<String> lore) {
		this.markDirty();
		this.lore = new ArrayList<>(lore);
		return this;
	}

	public ItemBuilder addLore(@NotNull MessageBuilder line) {
		return addLore(line.parse());
	}

	public ItemBuilder addLore(@NotNull String line) {
		this.markDirty();
		this.lore.add(line);
		return this;
	}

	public ItemBuilder addLore(@NotNull MessageBuilderList lore) {
		return this.addLore(lore.parse());
	}

	public ItemBuilder addLore(@NotNull Collection<String> lore) {
		this.markDirty();
		this.lore.addAll(lore);
		return this;
	}

	public ItemBuilder addLore(@NotNull MessageBuilderList lore, int index) {
		return this.addLore(lore.parse(), index);
	}

	public ItemBuilder addLore(@NotNull Collection<String> lore, int index) {
		this.markDirty();
		this.lore.addAll(index, lore);
		return this;
	}

	public ItemBuilder hideTooltips() {
		this.markDirty();
		this.hideToolTips = true;
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public ItemBuilder enchant(ResourceKey<Enchantment> enchantment, int level) {
		this.markDirty();
		this.enchantments.put(enchantment, level);
		return this;
	}

	public ItemBuilder customExecutor(CustomExecutor customExecutor) {
		this.markDirty();
		this.customExecutors.add(customExecutor);
		return this;
	}

	public <T> ItemBuilder dataComponent(DataComponentType<? super T> component, @Nullable T value) {
		this.markDirty();
		this.dataComponentMap.set(component, value);
		return this;
	}

	public ItemBuilder placeholders(Map<String, Object> placeholders) {
		this.markDirty();
		this.placeholders.putAll(placeholders);
		return this;
	}

	public ItemBuilder placeholder(String key, Object value) {
		this.markDirty();
		this.placeholders.put(key, value);
		return this;
	}

	public ItemStack build() {
		if (this.dirty || this.cache == null) {
			this.cache = internalBuild();
			this.dirty = false;
		}

		return this.cache;
	}

	public ItemStack internalBuild() {
		if (this.displayName != null) {
			String parsedDisplayName = new MessageBuilder(this.displayName)
					.parse(this.placeholders)
					.toString();
			Component nameComponent = CoreBackendModule.instance().getMiniMessageManager().parse(parsedDisplayName);
			this.baseItem.set(DataComponents.ITEM_NAME, nameComponent);
		}

		if (!this.lore.isEmpty()) {
			List<Component> straightLore = new ArrayList<>();
			List<String> parsedLore = new MessageBuilderList(this.lore)
					.parse(this.placeholders)
					.parse();

			for (String line : parsedLore) {
				Component component = CoreBackendModule.instance().getMiniMessageManager().parse(line);
				Style style = component.getStyle().withItalic(false);
				straightLore.add(component.copy().withStyle(style));
			}

			ItemLore nbtLore = new ItemLore(straightLore, straightLore);
			this.baseItem.set(DataComponents.LORE, nbtLore);
		}

		if (this.hideToolTips) {
			this.baseItem.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
		}

		if (this.skullOwner != null) {
			String parsedSkullOwner = new MessageBuilder(this.skullOwner)
					.parse(this.placeholders)
					.toString();
			ResolvableProfile profile = new ResolvableProfile(Optional.of(parsedSkullOwner), Optional.empty(), new PropertyMap());
			this.baseItem.set(DataComponents.PROFILE, profile);
		}

		enchantments.forEach((enchantment, level) -> {
			Optional<Registry<Enchantment>> optionalRegistry = CoreBackendModule.instance().getRegistryAccess().registry(Registries.ENCHANTMENT);
			if (optionalRegistry.isEmpty()) {
				return;
			}

			Registry<Enchantment> registry = optionalRegistry.get();
			Enchantment realEnchantment = registry.get(enchantment);

			if (realEnchantment == null) {
				return;
			}

			baseItem.enchant(registry.wrapAsHolder(realEnchantment), level);
		});

		customExecutors.forEach(customExecutor -> customExecutor.execute(this.baseItem));

		dataComponentMap.forEach((dataComponent) -> {
			//noinspection unchecked
			baseItem.set((DataComponentType<Object>) dataComponent.type(), dataComponent.value());
		});

		return this.baseItem;
	}

	public interface CustomExecutor {
		void execute(ItemStack itemStack);
	}

	public ItemBuilder copy() {
		return new ItemBuilder(
				this.baseItem.copy(),
				this.displayName,
				new ArrayList<>(this.lore),
				this.hideToolTips,
				this.skullOwner,
				new ArrayList<>(this.customExecutors),
				new HashMap<>(this.enchantments),
				this.dataComponentMap.copy(),
				new HashMap<>(this.placeholders)
		);
	}

	protected void markDirty() {
		this.dirty = true;
	}
}