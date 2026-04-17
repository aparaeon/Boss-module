package gg.mmorealms.module.gyms.backend.common.dto.gym.clauses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class GymInBattleClauses {
	@Builder.Default
	private boolean bagClause = false;

	public void apply(String clauseName) {
		switch (clauseName) {
			case "bag", "bag_clause" -> this.bagClause = true;
		}
	}
}