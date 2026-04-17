package gg.mmorealms.module.core.backend.common.utils;

import com.mojang.authlib.properties.PropertyMap;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
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
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO find a way to have the lore be straight (not italics)
public class ItemBuilder {

	private ItemStack itemStack;
	private Component displayName;
	private List<Component> lore = new ArrayList<>();
	private boolean hideToolTips = false;
	private final HashMap<ResourceKey<Enchantment>, Integer> enchantments = new HashMap<>();
	private final List<CustomExecutor> customExecutors = new ArrayList<>();
	private String skullOwner;
	private final PatchedDataComponentMap dataComponentMap = new PatchedDataComponentMap(DataComponentMap.EMPTY);

	private ItemBuilder() {
	}

	private ItemBuilder(GUIButton button) {
		this.display(CodecUtils.deserialize(ItemStack.CODEC, button.getDisplayJson(), CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)));

		if (button.getDisplayName() != null) {
			this.name(
					new MessageBuilder(button.getDisplayName())
							.parse(button.getPlaceholders())
			);
		}

		if (button.getLore() != null) {
			this.lore(
					new MessageBuilderList(button.getLore())
							.parse(button.getPlaceholders())
			);
		}

		if (button.getSkullOwner() != null) {
			this.skullOwner = new MessageBuilder(button.getSkullOwner())
					.parse(button.getPlaceholders())
					.parse();
		}
	}

	private ItemBuilder(ItemStack base) {
		this.itemStack = base;
	}

	public static ItemBuilder of() {
		return new ItemBuilder(ItemStack.EMPTY);
	}

	public static ItemBuilder of(ItemStack base) {
		return new ItemBuilder(base);
	}

	public static ItemBuilder of(GUIButton button) {
		return new ItemBuilder(button);
	}

	public ItemBuilder skullOwner(String skullOwner) {
		this.display("minecraft:player_head", 1);
		this.skullOwner = skullOwner;
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public ItemBuilder display(@NotNull String itemID, int amount) {
		Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemID));
		ItemStack itemStack = new ItemStack(item, amount);
		return display(itemStack);
	}

	public ItemBuilder display(@NotNull ItemStack display) {
		this.itemStack = display;
		return this;
	}

	@SuppressWarnings("unused")
	public ItemBuilder display(@NotNull Item item) {
		return display(item, 1);
	}

	public ItemBuilder display(@NotNull Item item, int count) {
		return display(new ItemStack(item, count));
	}

	public ItemBuilder name(@Nullable Component title) {
		this.displayName = title;
		return this;
	}

	public ItemBuilder name(@Nullable String title) {
		if (title == null) {
			this.displayName = null;
			return this;
		}
		this.displayName = CoreBackendModule.instance().getMiniMessageManager().parse(title);
		return this;
	}

	public ItemBuilder name(@NotNull MessageBuilder titleBuilder) {
		return this.name(titleBuilder.parse());
	}

	public ItemBuilder lore(@NotNull MessageBuilderList lore) {
		return this.lore(lore.parse());
	}

	public ItemBuilder lore(@NotNull Collection<String> lore) {
		this.lore = parseLore(lore);
		return this;
	}

	public ItemBuilder addLore(@NotNull MessageBuilder line) {
		return addLore(line.parse());
	}

	public ItemBuilder addLore(@NotNull String line) {
		this.lore.add(CoreBackendModule.instance().getMiniMessageManager().parse(line));
		return this;
	}

	public ItemBuilder addLore(@NotNull MessageBuilderList lore) {
		return this.addLore(lore.parse());
	}

	public ItemBuilder addLore(@NotNull Collection<String> lore) {
		this.lore.addAll(parseLore(lore));
		return this;
	}

	public ItemBuilder addLore(@NotNull MessageBuilderList lore, int index) {
		return this.addLore(lore.parse(), index);
	}

	public ItemBuilder addLore(@NotNull Collection<String> lore, int index) {
		this.lore.addAll(index, parseLore(lore));
		return this;
	}

	private List<Component> parseLore(Collection<String> lore) {
		List<Component> parsedLore = new ArrayList<>();
		for (String line : lore) {
			parsedLore.add(CoreBackendModule.instance().getMiniMessageManager().parse(line));
		}
		return parsedLore;
	}

	public ItemBuilder hideTooltips() {
		this.hideToolTips = true;
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public ItemBuilder enchant(ResourceKey<Enchantment> enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}

	public ItemBuilder customExecutor(CustomExecutor customExecutor) {
		this.customExecutors.add(customExecutor);
		return this;
	}

	public <T> ItemBuilder dataComponent(DataComponentType<? super T> component, @Nullable T value) {
		this.dataComponentMap.set(component, value);
		return this;
	}

	public ItemStack build() {
		if (this.displayName != null) {
//            this.itemStack.set(DataComponents.CUSTOM_NAME, this.displayName);
			this.itemStack.set(DataComponents.ITEM_NAME, this.displayName);
		}

		if (!this.lore.isEmpty()) {
			List<Component> straightLore = new ArrayList<>();

			for (Component component : this.lore) {
				Style style = component.getStyle().withItalic(false);
				straightLore.add(component.copy().withStyle(style));
			}

			ItemLore nbtLore = new ItemLore(straightLore, straightLore);
			this.itemStack.set(DataComponents.LORE, nbtLore);
		}

		if (this.hideToolTips) {
//            this.itemStack.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE);
			this.itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
		}

		if (this.skullOwner != null) {
			ResolvableProfile profile = new ResolvableProfile(Optional.of(this.skullOwner), Optional.empty(), new PropertyMap());
			this.itemStack.set(DataComponents.PROFILE, profile);
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

			itemStack.enchant(registry.wrapAsHolder(realEnchantment), level);
		});

		customExecutors.forEach(customExecutor -> customExecutor.execute(this.itemStack));

		dataComponentMap.forEach((a) -> {
			itemStack.set((DataComponentType<Object>) a.type(), a.value());
		});

		return this.itemStack;
	}

	public interface CustomExecutor {
		void execute(ItemStack itemStack);
	}
}