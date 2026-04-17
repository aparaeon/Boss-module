package com.raduvoinea.utils.generic.dto;

import java.util.concurrent.atomic.AtomicReference;

public class Holder<T> {
	private final AtomicReference<T> value;

	private Holder(T value) {
		this.value = new AtomicReference<>(value);
	}

	public static <T> Holder<T> of(T value) {
		return new Holder<>(value);
	}

	public static <T> Holder<T> empty() {
		return new Holder<>(null);
	}

	public T value() {
		return value.get();
	}

	public Holder<T> set(T newValue) {
		value.set(newValue);
		return this;
	}

	public boolean isEmpty() {
		return value.get() == null;
	}

}
