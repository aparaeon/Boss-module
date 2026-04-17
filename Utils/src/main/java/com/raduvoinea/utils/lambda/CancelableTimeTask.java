package com.raduvoinea.utils.lambda;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;

@Getter
public abstract class CancelableTimeTask {

	@Setter
	private @Nullable ScheduledFuture<?> future;
	private boolean canceled;

	public abstract void execute();

	public void run() {
		if (canceled) {
			cancel();
			return;
		}
		execute();
	}

	public boolean cancel() {
		this.canceled = true;
		if (future != null) {
			future.cancel(true);
		}

		return true;
	}
}
