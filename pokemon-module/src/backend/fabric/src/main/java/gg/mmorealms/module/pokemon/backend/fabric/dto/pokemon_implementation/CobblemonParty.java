package gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PartyPosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.SimpleSyncedNetworkObject;
import gg.mmorealms.loader.common.exception.SyncedRequestException;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;
import net.minecraft.core.RegistryAccess;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CobblemonParty extends SimpleSyncedNetworkObject<UUID, CobblemonParty> implements IPokemonParty {

	private final static RegistryAccess REGISTRY_ACCESS = PokemonBackendModule.instance().getRegistryAccess();

	private final PlayerPartyStore nativeParty;

	public CobblemonParty(PlayerPartyStore nativeParty) {
		super(CobblemonParty.class);
		this.nativeParty = nativeParty;
	}

	@Override
	public PlayerPartyStore getNative() {
		return nativeParty;
	}

	@Override
	public JsonObject serialize() {
		JsonObject jsonObject = new JsonObject();

		PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(nativeParty.getUuid(), REGISTRY_ACCESS);
		party.saveToJSON(jsonObject, REGISTRY_ACCESS);

		return jsonObject;
	}

	@Override
	public IPokemon getPokemon(int index) {
		Pokemon nativePokemon = nativeParty.get(index);

		if (nativePokemon == null) {
			return null;
		}

		return new CobblemonPokemon(nativePokemon);
	}

	@Override
	public Integer getSize() {
		return nativeParty.size();
	}

	@Override
	public void setPokemon(int index, IPokemon pokemon) {
		if (pokemon == null) {
			nativeParty.remove(new PartyPosition(index));
			return;
		}
		nativeParty.set(index, (Pokemon) pokemon.getNative());
	}


	@Override
	public void add(IPokemon pokemon) {
		add(pokemon.serialize());
	}

	@Override
	public void add(JsonObject serializedPokemon) {
		IUser userPrimitive = IUser.getByUUID(nativeParty.getPlayerUUID());
		if (!(userPrimitive instanceof User)) {
			if (!userPrimitive.isOnlineOnNetwork()) {
				if (tryAddToParty(serializedPokemon, userPrimitive)) {
					return;
				}

				if (!tryAddToPC(serializedPokemon, userPrimitive)) {
					Logger.error(new MessageBuilder("{user} did not have any space to receive {serialized_pokemon}")
							.parse("user", userPrimitive.getUsername())
							.parse("serialized_pokemon", serializedPokemon)
					);
				}

				return;
			}

			try {
				sendRequest(serializedPokemon);
			} catch (SyncedRequestException e) {
				Logger.error(e);
			}
			return;
		}

		IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().deserializePokemon(serializedPokemon);
		nativeParty.add((Pokemon) pokemon.getNative());
	}

	private boolean tryAddToParty(JsonObject serializedPokemon, IUser user) {
		String partySlot = findFirstNullPartySlot(user.getUUID());
		if (partySlot != null) {
			addPokemonToSlot(serializedPokemon, partySlot, user.getUUID(), "partyData");
			return true;
		}
		return false;
	}

	private boolean tryAddToPC(JsonObject serializedPokemon, IUser user) {
		String pcSlot = findFirstEmptyBoxSlot(user.getUUID());
		if (pcSlot != null) {
			addPokemonToSlot(serializedPokemon, pcSlot, user.getUUID(), "pcData");
			return true;
		}
		return false;
	}

	private void addPokemonToSlot(JsonObject serializedPokemon, String slot, UUID playerUUID, String dataColumn) {
		DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
			String slotPath = "$." + slot;

			String sql = new MessageBuilder("UPDATE core.pokemon_data SET {column} = JSON_INSERT({column}, ?, JSON_EXTRACT(?, '$')) WHERE uuid = ?")
					.parse("column", dataColumn)
					.parse();

			session.createNativeQuery(sql)
					.setParameter(1, slotPath)
					.setParameter(2, CommonLoader.instance().toJson(serializedPokemon))
					.setParameter(3, playerUUID)
					.executeUpdate();
		});
	}

	public String findFirstNullPartySlot(UUID uuid) {
		return findSlotInData(uuid, "partyData", this::findFirstNullSlot);
	}

	public String findFirstEmptyBoxSlot(UUID uuid) {
		return findSlotInData(uuid, "pcData", this::findFirstEmptySlot);
	}

	private String findSlotInData(UUID uuid, String column, SlotFinder finder) {
		String sql = new MessageBuilder("SELECT {column} FROM core.pokemon_data WHERE uuid = ?")
				.parse("column", column)
				.parse();

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			String dataJson = (String) session.createNativeQuery(sql)
					.setParameter(1, uuid)
					.getSingleResult();
			return finder.findSlot(dataJson);
		} catch (Exception e) {
			throw new RuntimeException("Error finding slot in " + column, e);
		}
	}

	private String findFirstNullSlot(String partyDataJson) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(partyDataJson);

		int slotCount = rootNode.path("SlotCount").asInt(0);
		if (slotCount <= 0) {
			return null;
		}

		for (int i = 0; i < slotCount; i++) {
			String slotKey = "Slot" + i;
			JsonNode slotNode = rootNode.get(slotKey);

			if (slotNode == null || slotNode.isNull()) {
				return slotKey;
			}
		}

		return null;
	}

	private String findFirstEmptySlot(String pcDataJson) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(pcDataJson);

		int boxCount = rootNode.path("BoxCount").asInt(0);
		if (boxCount <= 0) {
			return null;
		}

		for (int boxIndex = 0; boxIndex < boxCount; boxIndex++) {
			String boxKey = "Box" + boxIndex;
			JsonNode boxNode = rootNode.get(boxKey);

			if (boxNode == null || boxNode.isNull() || boxNode.isEmpty()) {
				return boxKey + ".Slot0";
			}

			String emptySlot = findEmptySlotInBox(boxNode, boxKey);
			if (emptySlot != null) {
				return emptySlot;
			}
		}

		return null;
	}

	private String findEmptySlotInBox(JsonNode boxNode, String boxKey) {
		for (int slotIndex = 0; slotIndex < 30; slotIndex++) {
			String slotKey = "Slot" + slotIndex;
			JsonNode slotNode = boxNode.get(slotKey);

			if (slotNode == null || slotNode.isNull()) {
				return boxKey + "." + slotKey;
			}
		}
		return null;
	}

	@Override
	public @Nullable CobblemonParty getByIdentifier(@NotNull UUID uuid) {
		return (CobblemonParty) IPokemonParty.get(uuid);
	}

	@Override
	public @Nullable UUID getIdentifier() {
		return nativeParty.getPlayerUUID();
	}

	@Override
	public String getDestinationServer() {
		IUser userPrimitive = IUser.getByUUID(nativeParty.getPlayerUUID());
		return userPrimitive.getServerLocation().getServer();
	}

	@FunctionalInterface
	private interface SlotFinder {
		String findSlot(String jsonData) throws Exception;
	}
}
