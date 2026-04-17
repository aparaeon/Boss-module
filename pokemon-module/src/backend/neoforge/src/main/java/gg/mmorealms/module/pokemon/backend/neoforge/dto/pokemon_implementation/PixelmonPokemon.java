package gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation;

import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.api.pokemon.Nature;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.pixelmonmod.pixelmon.items.helpers.ItemHelper;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.neoforge.PokemonNeoForgeModule;
import gg.mmorealms.module.pokemon.backend.neoforge.manager.PixelmonPrintUtils;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

@Getter
public class PixelmonPokemon implements IPokemon {

	private final Pokemon nativePokemon;

	public PixelmonPokemon(Pokemon nativePokemon) {
		this.nativePokemon = nativePokemon;
	}

	@Override
	public Pokemon getNative() {
		return nativePokemon;
	}

	@Override
	public JsonObject serialize() {
		CompoundTag nbt = new CompoundTag();
		nativePokemon.writeToNBT(nbt, PokemonNeoForgeModule.instance().getRegistryAccess());

		return CodecUtils.serialize(CompoundTag.CODEC, nbt);
	}

	@Override
	public String getSpeciesString() {
		return getSpeciesName();
	}

	@Override
	public String getSpeciesName() {
		return nativePokemon.getSpecies().getName();
	}

	@Override
	public String getName() {
		return getName(false);
	}

	@Override
	public String getName(boolean ignored) {
		return nativePokemon.getDisplayName().getString();
	}

	@Override
	public int getLevel() {
		return nativePokemon.getPokemonLevel();
	}

	@Override
	public boolean isShiny() {
		return nativePokemon.isShiny();
	}

	@Override
	public void setShiny(boolean shiny) {
		nativePokemon.setShiny(shiny);
	}

	@Override
	public boolean isMega() {
		return nativePokemon.isMega();
	}

	@Override
	public boolean isNeutered() {
		return false;
	}

	@Override
	public void setNeutered(boolean neutered) {
	}

	@Override
	public String getBallString() {
		ItemStack stack = nativePokemon.getBall().getBallItem();
		if (stack.getItem().equals(Items.AIR)) {
			return "None";
		}

		return stack.getHoverName().getString();
		// TODO: Might need this: .replace("_", " ");
	}

	@Override
	public String getHeldItemString() {
		ItemStack stack = nativePokemon.getHeldItem();
		if (stack.getItem().equals(Items.AIR)) {
			return "None";
		}

		return stack.getHoverName().getString();
		// TODO: Might need this: .replace("_", " ");
	}

    @Override
    public List<String> getNatures() {
        return Arrays.stream(Nature.values()).map(Enum::name).toList();
    }

	@Override
	public String getNatureString() {
		if (nativePokemon.getNature() == null) {
			return "Unknown";
		}
		return nativePokemon.getNature().getTranslatedName().getString();
	}

	@Override
	public String getAbilityID() {
		if (nativePokemon.getAbility() == null) {
			return "Unknown";
		}
		return nativePokemon.getAbility().getName();
	}

	@Override
	// TODO: Might need to modify this
	public String getAbilityName() {
		if (nativePokemon.getAbility() == null) {
			return "Unknown";
		}
		return nativePokemon.getAbility().getName();
	}

    @Override
    public List<String> getPossibleGenders() {
        return this.nativePokemon.getForm().getPossibleGenders().stream()
                .map(Enum::name)
                .toList();
    }

	@Override
	public String getGenderString() {
		return nativePokemon.getGender().name();
	}

	@Override
	public int getIvsHP() {
		return nativePokemon.getIVs().getStat(BattleStatsType.HP);
	}

	@Override
	public int getIvsAttack() {
		return nativePokemon.getIVs().getStat(BattleStatsType.ATTACK);
	}

	@Override
	public int getIvsDefence() {
		return nativePokemon.getIVs().getStat(BattleStatsType.DEFENSE);
	}

	@Override
	public int getIvsSpecialAttack() {
		return nativePokemon.getIVs().getStat(BattleStatsType.SPECIAL_ATTACK);
	}

	@Override
	public int getIvsSpecialDefence() {
		return nativePokemon.getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
	}

	@Override
	public int getIvsSpeed() {
		return nativePokemon.getIVs().getStat(BattleStatsType.SPEED);
	}

	@Override
	public int getIvsTotal() {
		return IPokemon.super.getIvsTotal();
	}

	@Override
	public int getIvsAverage() {
		return IPokemon.super.getIvsAverage();
	}

	@Override
	public int getEvsHP() {
		return nativePokemon.getEVs().getStat(BattleStatsType.HP);
	}

	@Override
	public int getEvsAttack() {
		return nativePokemon.getEVs().getStat(BattleStatsType.ATTACK);
	}

	@Override
	public int getEvsDefence() {
		return nativePokemon.getEVs().getStat(BattleStatsType.DEFENSE);
	}

	@Override
	public int getEvsSpecialAttack() {
		return nativePokemon.getEVs().getStat(BattleStatsType.SPECIAL_ATTACK);
	}

	@Override
	public int getEvsSpecialDefence() {
		return nativePokemon.getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
	}

	@Override
	public int getEvsSpeed() {
		return nativePokemon.getEVs().getStat(BattleStatsType.SPEED);
	}

	@Override
	public int getEvsTotal() {
		return IPokemon.super.getEvsTotal();
	}

	@Override
	public int getEvsAverage() {
		return IPokemon.super.getEvsAverage();
	}

	@Override
	public List<String> getMovesNames() {
		List<String> output = new ArrayList<>();
		for (Attack attack : nativePokemon.getMoveset()) {
			output.add(attack.getType().getRegisteredName());
		}

		for (int i = output.size(); i < 4; i++) {
			output.add("None");
		}

		return output;
	}

	@Override
	// TODO: Might need to modify this
	public List<String> getMovesIDs() {
		List<String> output = new ArrayList<>();
		for (Attack attack : nativePokemon.getMoveset()) {
			output.add(attack.getMove().getAttackName());
		}

		for (int i = output.size(); i < 4; i++) {
			output.add("None");
		}

		return output;
	}

	@Override
	public ItemStack toItemStack() {
		ItemStack itemStack = new ItemStack(ItemRegistration.PIXELMON_SPRITE.value());

		CompoundTag tagCompound = new CompoundTag();
		tagCompound.putShort("ndex", (short) nativePokemon.getSpecies().getDex());
		tagCompound.putString("form", nativePokemon.getForm().getName());
		tagCompound.putByte("gender", (byte) nativePokemon.getGender().ordinal());
		tagCompound.putString("palette", nativePokemon.getPalette().getName());
		if (nativePokemon.isEgg()) {
			tagCompound.putInt("eggCycles", nativePokemon.getEggCycles());
		}

		if (nativePokemon.getNickname() != null && !nativePokemon.getNickname().getString().isEmpty()) {
			tagCompound.putString("Nickname", nativePokemon.getNickname().getString());
		}

		tagCompound.putString("json", this.serialize().toString());

		//noinspection removal
		ItemHelper.setTag(itemStack, tagCompound);
		return itemStack;
	}

	@Override
	public String getColor() {
		// TODO
		return "Unspecified";
	}

    @Override
    public void applyNameStyle(String miniMessage) {
        // TODO
    }

    @Override
    public void resetNameStyle() {
        // TODO
    }


    @Override
	public void setColor(String color) {
		Logger.error("THERE IS NO WAY");
		//TODO
	}

	@Override
	public void heal() {
		nativePokemon.heal();
	}

	@Override
	public float getScale() {
		return (float) nativePokemon.getSize();
	}

	@Override
	public void setScale(float scale) {
		nativePokemon.setSize(scale);
	}

	@Override
	public PixelmonEntity createEntity(ServerLevel level) {
		return new PixelmonEntity(level, nativePokemon);
	}

	@Override
	public Set<String> getAspects() {
//		return nativePokemon.getAspects();
		// TODO
		return new HashSet<String>();
	}

	@Override
	// TODO: Test this
	public String getBriefDescription() {
		return PixelmonPrintUtils.getBriefDescription(nativePokemon);
	}
}
