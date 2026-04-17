package gg.mmorealms.module.auction_house.backend.common.dto.database;

import com.google.gson.JsonObject;
import com.raduvoinea.utils.file_manager.utils.DateUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.redis_manager.dto.LockableResource;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.backend.common.utils.ItemUtils;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.config.AuctionHouseConfig;
import gg.mmorealms.module.auction_house.backend.common.dto.AuctionHouseCategory;
import gg.mmorealms.module.auction_house.backend.common.exception.RateLimitException;
import gg.mmorealms.module.auction_house.backend.common.manager.AuctionHouseEntriesManager;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryRemovedEvent;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

// TODO Split auction_house_entries into pokemon_auction_house_entry and item_auction_house_entry
@Entity(name = "auction_house_entries")
@Getter
@NoArgsConstructor
public class AuctionHouseEntry implements IDatabaseEntry<Long>, LockableResource {

	@Id
	@jakarta.validation.constraints.NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private @Getter long id;

	public UUID ownerUUID;
	public String ownerName;
	// Category
	public String category;
	// Data
	public Type type;
	@JdbcTypeCode(SqlTypes.JSON)
	public JsonObject json;
	// Display
	public int price;
	// Metadata
	public long date;

	private transient ItemStack displayItem;

	private AuctionHouseEntry(long id) {
		this.id = id;
	}

	public static AuctionHouseEntry getDummy(Long id) {
		return new AuctionHouseEntry(id);
	}

	public AuctionHouseEntry(User user, Type type, ItemStack itemStack, Integer price) throws RateLimitException {
		this(user, type, CodecUtils.serialize(ItemStack.CODEC, itemStack), price);
	}

	public AuctionHouseEntry(User user, Type type, IPokemon pokemon, Integer price) throws RateLimitException {
		this(user, type, pokemon.serialize(), price);
	}

	@SneakyThrows
	public AuctionHouseEntry(User user, Type type, JsonObject json, Integer price) {
		AuctionHouseEntriesManager.checkRateLimit();

		this.ownerUUID = user.getUUID();
		this.ownerName = user.getUsername();

		this.type = type;
		this.json = json;
		this.price = price;
		this.date = System.currentTimeMillis();

		displayItem = generateDisplayItem();

		if (type == Type.POKEMON) {
			AuctionHouseCategory category = AuctionHouseBackendModule.instance().getConfig().getCategory("pokemon");
			if (category != null) {
				this.category = category.getName();
			} else {
				this.category = getCategory(displayItem);
			}
		} else {
			this.category = getCategory(displayItem);
		}

		Logger.info(new MessageBuilder("{username} ({uuid}) listed {item} (id: {id}) for {amount} {currency}")
				.parse("username", user.getUsername())
				.parse("uuid", user.getUUID().toString())
				.parse("item", getDetailedLog())
				.parse("id", getId())
				.parse("amount", price)
				.parse("currency", AuctionHouseBackendModule.instance().getConfig().currency.getName()));
	}

	private static String getCategory(ItemStack itemStack) {
		if (itemStack.isEnchanted()) {
			AuctionHouseCategory category = AuctionHouseBackendModule.instance().getConfig().getCategory("enchanting");
			if (category != null) {
				return category.getName();
			}
		}

		Item item = itemStack.getItem();
		String itemID = BuiltInRegistries.ITEM.getKey(item).getPath().toLowerCase();


		for (AuctionHouseCategory category : AuctionHouseBackendModule.instance().getConfig().categories) {
			for (String compatibleID : category.getItems()) {
				if (itemID.equals(compatibleID)) {
					return category.getName();
				}
			}
		}

		return AuctionHouseBackendModule.instance().getConfig().otherCategory.getName();
	}

	private ItemStack generateDisplayItem() {
		ItemBuilder itemBuilder;

		long expires = date + AuctionHouseBackendModule.instance().getConfig().auctionDuration.toMilliseconds();
		long timeUntilExpires = expires - System.currentTimeMillis();

		List<String> additionalLore = AuctionHouseBackendModule.instance().getConfig().lang.entryLore
				.parse("owner", ownerName)
				.parse("price", NumberUtils.formatNumberWithCommas(getPrice()))
				.parse("expires", timeUntilExpires <= 0 ? "Expired" : DateUtils.convertToPeriod(expires - System.currentTimeMillis()))
				.parse();

		if (type == Type.ITEM) {
			itemBuilder = ItemBuilder.of(CodecUtils.deserialize(ItemStack.CODEC, json, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)));
		} else {
			itemBuilder = PokemonBackendModule.instance().getPlatformImplementation().deserializePokemon(this.json).toItemBuilder(true);
		}

		itemBuilder.hideTooltips().addLore(additionalLore, 0).build();

		return itemBuilder.build();
	}

	public String getRedisLockID() {
		return "auction_house_lock_" + id;
	}

	@Override
	public RedisManager getRedisManager() {
		return AuctionHouseBackendModule.instance().getRedisManager();
	}

	@Override
	public Time getLockTime() {
		return Time.minutes(1);
	}

	@SneakyThrows
	public void sell(User user) {
		boolean isSelf = ownerUUID.equals(user.getUUID());
		if (!AuctionHouseBackendModule.instance().getEntriesManager().hasEntryInCache(this) && !isSelf) {
			sendLockedPurchaseMessage(user);
			return;
		}

		withLock(() -> {
			IUser owner = IUser.getByUUID(this.ownerUUID);

			IBalances userCurrencies = IBalances.getByUser(user);
			IBalances ownerCurrencies = IBalances.getByUser(owner);

			AuctionHouseConfig config = AuctionHouseBackendModule.instance().getConfig();

			if (!isSelf) {
				if (userCurrencies.get(config.currency) < price) {
					user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.notEnoughMoney);
					return;
				}

				ownerCurrencies.add(config.currency, price * 1.0, "AUCTION_HOUSE");
				userCurrencies.add(config.currency, -price * 1.0, "AUCTION_HOUSE");
			}

			AuctionHouseBackendModule.instance().getEntriesManager().deleteEntryFromCache(this);
			try {
				delete();
			} catch (Throwable e) {
				Logger.error(new MessageBuilder("There was a problem deleting the auction house entry with id {id} while player {username} ({uuid}) tried to purchase it, will refund the balance transactions")
						.parse("id", this.getId()).parse("username", user.getUsername())
						.parse("uuid", user.getUUID().toString()));
				user.sendMessage("<red>There was an error regarding your purchase, please make a ticket on discord.");

				ownerCurrencies.add(config.currency, -price * 1.0, "AUCTION_HOUSE_PURCHASE_BUG");
				userCurrencies.add(config.currency, price * 1.0, "AUCTION_HOUSE_PURCHASE_BUG");

				Logger.error(e);
				return;
			}

			give(user);

			if (isSelf) {
				Logger.info(new MessageBuilder("{username} ({uuid}) got back {item} (id: {id})").parse("username", ownerName)
						.parse("uuid", ownerUUID.toString())
						.parse("id", getId())
						.parse("item", getDetailedLog()));

				user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.gotBack);
			} else {
				Logger.info(new MessageBuilder("{username1} ({uuid1}) bought {item} (id: {id}) from {username2} ({uuid2}) with {amount} {currency}")
						.parse("username1", user.getUsername())
						.parse("uuid1", user.getUUID().toString())
						.parse("item", getDetailedLog())
						.parse("id", getId())
						.parse("username2", ownerName).parse("uuid2", ownerUUID.toString())
						.parse("amount", price)
						.parse("currency", config.currency.getName()));

				user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.purchased);
				owner.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.itemSold);

				IBackendCooldowns cooldown = IBackendCooldowns.getByUser(user);
				cooldown.set("ah_cooldown", Time.minutes(1));
			}

			Logger.info("The auction entry with id " + this.id + " has been removed.");
			new AuctionHouseEntryRemovedEvent(this.id).send();
		}, () -> sendLockedPurchaseMessage(user));
	}

	private void sendLockedPurchaseMessage(User user) {
		AuctionHouseConfig config = AuctionHouseBackendModule.instance().getConfig();

		Logger.info(new MessageBuilder("{username1} ({uuid1}) tried to buy {item} (id: {id}) from {username2} ({uuid2}) with {amount} {currency} but it is in a locked state")
				.parse("username1", user.getUsername())
				.parse("uuid1", user.getUUID().toString())
				.parse("item", getDetailedLog())
				.parse("id", getId())
				.parse("username2", ownerName).parse("uuid2", ownerUUID.toString())
				.parse("amount", price)
				.parse("currency", config.currency.getName()));

		user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.locked);
	}

	public void give(User user) {
		if (type == Type.ITEM) {
			ItemStack itemstack = CodecUtils.deserialize(ItemStack.CODEC, json, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));

			if (itemstack == null) {
				user.sendMessage(AuctionHouseBackendModule.instance().getConfig().lang.deserializationIssue);
			} else {
				user.getPlayer().getInventory().placeItemBackInInventory(itemstack);
			}
		} else {
			IPokemonParty party = PokemonBackendModule.instance().getPlatformImplementation().getParty(user.getPlayer());
			party.add(PokemonBackendModule.instance().getPlatformImplementation().deserializePokemon(this.json));
		}
	}

	private String getDetailedLog() {
		if (type == Type.ITEM) {
			ItemStack itemstack = CodecUtils.deserialize(ItemStack.CODEC, json, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));

			if (itemstack == null) {
				return getEntryName();
			}

			itemstack.get(DataComponents.UNBREAKABLE);

			IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().getPokemonFromItem(itemstack);
			if (pokemon == null) {
				return ItemUtils.getLoggingDescription(itemstack);
			}

			// TODO: Use getSpeciesID() instead after merging eggs and incubators
			return new MessageBuilder("{shiny?}{plushie} plushie")
					.parse("shiny?", pokemon.isShiny() ? "Shiny " : "")
					.parse("plushie", pokemon.getSpeciesName()).parse();
		} else {
			return PokemonBackendModule.instance().getPlatformImplementation().deserializePokemon(this.json).getBriefDescription();
		}
	}

	private String getEntryName() {
		if (type == Type.POKEMON) {
			return PokemonBackendModule.instance().getPlatformImplementation().deserializePokemon(this.json).getSpeciesName();
		}

		ItemStack itemstack = CodecUtils.deserialize(ItemStack.CODEC, json, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
		if (itemstack == null) {
			return null;
		}

		return itemstack.getDisplayName().getString();
	}

	public ItemStack getDisplayItem() {
		if (displayItem == null) {
			displayItem = generateDisplayItem();
		}

		return displayItem;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		AuctionHouseEntry that = (AuctionHouseEntry) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public Long getIdentifier() {
		return id;
	}

	@Override
	public DatabaseLoader<Long, ?, ?> getLoader() {
		return null;
	}

	public enum Type {
		ITEM, POKEMON
	}
}
