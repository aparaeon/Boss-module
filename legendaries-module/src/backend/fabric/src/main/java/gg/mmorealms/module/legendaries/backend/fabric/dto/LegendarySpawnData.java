package gg.mmorealms.module.legendaries.backend.fabric.dto;

import com.google.gson.annotations.SerializedName;
import com.raduvoinea.utils.generic.dto.IWeighted;
import gg.mmorealms.module.legendaries.backend.fabric.dto.enums.TimeOfDay;
import gg.mmorealms.module.legendaries.backend.fabric.dto.enums.Weather;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * Class representation of a legendary spawn config JSON file
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LegendarySpawnData extends HashMap<String, LegendarySpawnData.PokemonData> {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class PokemonData implements IWeighted {
		private String spec;
		private double weight;
		private Conditions conditions;
		private List<String> biomes;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Conditions {
		private List<TimeOfDay> time;
		private Weather weather;
		private boolean underground;
		private boolean underwater;
		@SerializedName("max-y")
		private int maxY;

		public boolean matchesAnyTime(long worldTime) {
			if (time == null || time.isEmpty() || time.contains(TimeOfDay.ANY)) return true;
			return time.stream().anyMatch(t -> t.matches(worldTime));
		}

		public List<TimeOfDay> expandTimes() {
			if (this.time == null || this.time.isEmpty() || this.time.contains(TimeOfDay.ANY)) {
				return List.of(TimeOfDay.values());
			}
			return this.time;
		}

		public List<Weather> expandWeathers() {
			if (this.weather == Weather.ANY) {
				return List.of(Weather.CLEAR, Weather.RAIN, Weather.STORM);
			}
			return List.of(this.weather);
		}
	}
}