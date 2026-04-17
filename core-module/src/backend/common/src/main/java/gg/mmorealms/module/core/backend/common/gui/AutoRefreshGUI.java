package gg.mmorealms.module.core.backend.common.gui;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.utils.TimeUtils;
import lombok.Setter;

@Setter
public abstract class AutoRefreshGUI extends GUI {
	protected int autoRefreshIntervalTick = 20;
	protected int autoRefreshCurrentTick = 0;

	public AutoRefreshGUI(User user, Settings settings) {
		super(user, settings);
	}

	public AutoRefreshGUI(User user, Settings settings, int autoRefreshIntervalTick) {
		super(user, settings);
		this.autoRefreshIntervalTick = autoRefreshIntervalTick;
	}

	public AutoRefreshGUI(User user, Settings settings, Time autoRefreshInterval) {
		super(user, settings);
		setAutoRefreshInterval(autoRefreshInterval);
	}

	protected void setAutoRefreshInterval(Time autoRefreshInterval) {
		this.autoRefreshIntervalTick = TimeUtils.timeToTick(autoRefreshInterval);
	}

	@Override
	public void onTick() {
		if (++autoRefreshCurrentTick >= autoRefreshIntervalTick && shouldAutoRefresh()) {
			autoRefresh();
			autoRefreshCurrentTick = 0;
		}
	}

	public boolean shouldAutoRefresh() {
		return true;
	}

	public void autoRefresh() {
		beforeAutoRefresh();

		refresh();

		afterAutoRefresh();
	}

	protected void beforeAutoRefresh() {

	}

	protected void afterAutoRefresh() {

	}

}
