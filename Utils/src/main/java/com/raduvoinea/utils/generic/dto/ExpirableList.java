package com.raduvoinea.utils.generic.dto;

import com.raduvoinea.utils.generic.Time;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ExpirableList<Data> {

	private final List<ExpirableData<Data>> datas = Collections.synchronizedList(new ArrayList<>());
	private final Time cooldownTime;

	public ExpirableList(Time cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void clearExpired() {
		datas.removeIf(data -> System.currentTimeMillis() - data.timestamp > cooldownTime.toMilliseconds());
	}

	public void add(Data data) {
		datas.add(new ExpirableData<>(data));
	}

	public List<Data> getData() {
		clearExpired();
		List<Data> data = new ArrayList<>();

		for (ExpirableData<Data> expirableData : datas) {
			data.add(expirableData.data);
		}

		return data;
	}

	public void removeIf(Predicate<? super Data> filter) {
		datas.removeIf(data -> filter.test(data.data));
	}

	public void remove(Data data) {
		datas.removeIf(expirableData -> expirableData.data.equals(data));
	}

	public boolean contains(Data data) {
		clearExpired();
		return datas.stream().anyMatch(expirableData -> expirableData.data.equals(data));
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
