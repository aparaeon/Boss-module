package gg.mmorealms.module.gyms.backend.common.dto;

import com.raduvoinea.utils.file_manager.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CooldownInfo {
	private String cooldownId;
	private String cooldownDisplay;
	private long remainingTime;

	public String getFormattedTime() {
		return DateUtils.convertToPeriod(remainingTime);
	}
}
