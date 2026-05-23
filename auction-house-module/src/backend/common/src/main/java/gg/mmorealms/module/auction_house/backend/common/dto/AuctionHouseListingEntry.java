package gg.mmorealms.module.auction_house.backend.common.dto;

import com.google.gson.JsonObject;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.world.item.ItemStack;

public sealed interface AuctionHouseListingEntry permits AuctionHouseListingEntry.Pokemon, AuctionHouseListingEntry.Item {
	JsonObject serialize();

	ItemStack getDisplayItem();

	record Pokemon(IPokemon pokemon) implements AuctionHouseListingEntry {
		@Override
		public JsonObject serialize() {
			return pokemon.serialize();
		}

		@Override
		public ItemStack getDisplayItem() {
			return pokemon.toItemStack();
		}
	}

	record Item(ItemStack itemStack) implements AuctionHouseListingEntry {
		@Override
		public JsonObject serialize() {
			return CodecUtils.serialize(ItemStack.CODEC, itemStack);
		}

		@Override
		public ItemStack getDisplayItem() {
			return itemStack.copy();
		}
	}
}