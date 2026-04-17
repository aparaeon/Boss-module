package com.raduvoinea.utils.generic.dto;

import com.raduvoinea.utils.generic.Time;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpirableCount {

	private final List<Long> datas = Collections.synchronizedList(new ArrayList<>());
	private final Time cooldownTime;

	public ExpirableCount(Time cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void clearExpired() {
		datas.removeIf(data -> System.currentTimeMillis() - data > cooldownTime.toMilliseconds());
	}

	public void add() {
		datas.add(System.currentTimeMillis());
	}

	public int size() {
		clearExpired();
		return datas.size();
	}

	@Getter
	public static class ExpirableData<Data> {
		private final Data data;
		private final long timestamp;

		public ExpirableData(Data data) {
			this.data = data;
			this.timestamp = System.currentTimeMillis();
		}
	}

}
