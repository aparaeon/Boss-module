package gg.mmorealms.module.login_rewards.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DailyGuiDay {

	private final int day;
	private final int rewardDay;
	private final String requiredPlaytime;
	private final String displayJson;
	private final List<String> rewardLore;
	private final boolean current;
	private final boolean claimed;
	private final boolean claimable;

}
