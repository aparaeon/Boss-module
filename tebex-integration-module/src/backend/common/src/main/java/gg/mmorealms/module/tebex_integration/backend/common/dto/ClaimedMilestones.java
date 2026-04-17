package gg.mmorealms.module.tebex_integration.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClaimedMilestones {

	private List<Entry> entries = new ArrayList<>();

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Entry {

		private long timestamp;
		private String id;

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;

			Entry entry = (Entry) o;
			return Objects.equals(id, entry.id);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(id);
		}
	}

	public void add(Entry milestone) {
		if (entries == null) {
			entries = new ArrayList<>();
		}

		if (!(entries instanceof ArrayList<Entry>)) {
			this.entries = new ArrayList<>(this.entries);
		}

		entries.add(milestone);
	}

	public boolean contains(String id) {
		return this.entries.stream().anyMatch(entry -> entry.getId().equals(id));
	}

}
