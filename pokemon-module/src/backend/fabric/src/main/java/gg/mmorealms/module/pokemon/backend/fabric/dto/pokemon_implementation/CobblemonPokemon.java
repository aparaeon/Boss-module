package gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import gg.mmorealms.module.pokemon.backend.common.utils.PokemonNameFormatter;
import gg.mmorealms.module.pokemon.backend.fabric.manager.CobblemonPrintUtils;
import lombok.Getter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class CobblemonPokemon implements IPokemon {

	private final Pokemon nativePokemon;

	public CobblemonPokemon(Pokemon nativePokemon) {
		this.nativePokemon = nativePokemon;
	}

	@Override
	public Pokemon getNative() {
		return nativePokemon;
	}

	@Override
	public JsonObject serialize() {
		return CodecUtils.serialize(Pokemon.getCODEC(), nativePokemon);
	}

	@Override
	public String getSpeciesString() {
		return nativePokemon.getSpecies().toString();
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
	public String getName(boolean showTitle) {
		return nativePokemon.getDisplayName(showTitle).getString();
	}

	@Override
	public int getLevel() {
		return nativePokemon.getLevel();
	}

	@Override
	public boolean isShiny() {
		return nativePokemon.getShiny();
	}

	@Override
	public void setShiny(boolean shiny) {
		nativePokemon.setShiny(shiny);
	}

	@Override
	public boolean isMega() {
		Set<String> aspects = nativePokemon.getForcedAspects();
		return aspects.contains(MEGA_KEY) || aspects.contains(MEGA_X_KEY) || aspects.contains(MEGA_Y_KEY);
	}

	@Override
	public boolean isNeutered() {
		return nativePokemon.getForcedAspects().contains(NEUTERED_KEY);
	}

	@Override
	public void setNeutered(boolean neutered) {
		Set<String> aspects = new HashSet<>(nativePokemon.getForcedAspects());

		if (neutered) {
			aspects.add(NEUTERED_KEY);
		} else {
			aspects.remove(NEUTERED_KEY);
		}

		nativePokemon.setForcedAspects(aspects);
	}

	@Override
	public List<String> getNatures() {
		return Natures.all().stream()
			.map((nature) -> nature.getName().getPath())
			.toList();
	}

	@Override
	public String getNatureString() {
		return nativePokemon.getNature().getName().getPath().replace("_", " ");
	}

	@Override
	public String getBallString() {
		return nativePokemon.getCaughtBall().getName().getPath().replace("_", " ");
	}

	@Override
	public String getHeldItemString() {
		ItemStack stack = nativePokemon.getHeldItem$common();
		if (stack.getItem().equals(Items.AIR)) {
			return "None";
		}

		return stack.getHoverName().getString();
	}

	@Override
	public String getAbilityID() {
		return nativePokemon.getAbility().getName();
	}

	public String getAbilityName() {
		return nativePokemon.getAbility().getDisplayName();
	}

	@Override
	public List<String> getPossibleGenders() {
		return this.nativePokemon.getSpecies().getPossibleGenders().stream()
			.map(Gender::getSerializedName)
			.toList();
	}

	@Override
	public String getGenderString() {
		return nativePokemon.getGender().name();
	}

	public int getIV(Stat stat) {
		Integer iv = nativePokemon.getIvs().get(stat);

		if (iv == null) {
			return 0;
		}

		return iv;
	}

	@Override
	public int getIvsHP() {
		return getIV(Stats.HP);
	}

	@Override
	public int getIvsAttack() {
		return getIV(Stats.ATTACK);
	}

	@Override
	public int getIvsDefence() {
		return getIV(Stats.DEFENCE);
	}

	@Override
	public int getIvsSpecialAttack() {
		return getIV(Stats.SPECIAL_ATTACK);
	}

	@Override
	public int getIvsSpecialDefence() {
		return getIV(Stats.SPECIAL_DEFENCE);
	}

	@Override
	public int getIvsSpeed() {
		return getIV(Stats.SPEED);
	}

	@Override
	public int getIvsTotal() {
		return IPokemon.super.getIvsTotal();
	}

	@Override
	public int getIvsAverage() {
		return IPokemon.super.getIvsAverage();
	}

	public int getEV(Stat stat) {
		Integer ev = nativePokemon.getEvs().get(stat);

		if (ev == null) {
			return 0;
		}

		return ev;
	}

	@Override
	public int getEvsHP() {
		return getEV(Stats.HP);
	}

	@Override
	public int getEvsAttack() {
		return getEV(Stats.ATTACK);
	}

	@Override
	public int getEvsDefence() {
		return getEV(Stats.DEFENCE);
	}

	@Override
	public int getEvsSpecialAttack() {
		return getEV(Stats.SPECIAL_ATTACK);
	}

	@Override
	public int getEvsSpecialDefence() {
		return getEV(Stats.SPECIAL_DEFENCE);
	}

	@Override
	public int getEvsSpeed() {
		return getEV(Stats.SPEED);
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

		for (Move move : nativePokemon.getMoveSet().getMoves()) {
			output.add(move.getDisplayName().getString());
		}

		for (int i = output.size(); i < 4; i++) {
			output.add("None");
		}

		return output;
	}

	@Override
	public List<String> getMovesIDs() {
		List<String> output = new ArrayList<>();

		for (Move move : nativePokemon.getMoveSet().getMoves()) {
			output.add(move.getName());
		}

		for (int i = output.size(); i < 4; i++) {
			output.add("none");
		}

		return output;
	}

	@Override
	public ItemStack toItemStack() {
		return PokemonItem.from(nativePokemon);
	}

	@Override
	public String getColor() {
		for (String aspect : nativePokemon.getAspects()) {
			String[] split = aspect.split("-");
			if (split[0].equals("color")) {
				return split[1];
			}
		}

		return "Unspecified";
	}

	@Override
	public void setColor(String color) {
		PokemonProperties properties = new PokemonProperties();

		properties.setCustomProperties(List.of(
			new StringSpeciesFeature("color", color)
		));

		properties.apply(nativePokemon);
	}

	@Override
	public void applyNameStyle(String input) {
		String fallbackName = nativePokemon.getNickname() != null
			? nativePokemon.getNickname().getString()
			: nativePokemon.getDisplayName(false).getString();

		MutableComponent result = PokemonNameFormatter.formatToNms(input, fallbackName);

		nativePokemon.setNickname(result);
	}

	@Override
	public void resetNameStyle() {
		nativePokemon.setNickname(null);
	}

	@Override
	public void heal() {
		nativePokemon.heal();
	}

	@Override
	public float getScale() {
		return nativePokemon.getScaleModifier();
	}

	@Override
	public void setScale(float scale) {
		nativePokemon.setScaleModifier(scale);
	}

	@Override
	public Entity createEntity(ServerLevel level) {
		return new PokemonEntity(level, nativePokemon, CobblemonEntities.POKEMON);
	}

	@Override
	public Set<String> getAspects() {
		return nativePokemon.getAspects();
	}

	@Override
	public String getBriefDescription() {
		return CobblemonPrintUtils.getBriefDescription(nativePokemon);
	}
}
