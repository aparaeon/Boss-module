package com.raduvoinea.utils.generic.dto;

public record Pair2<First, Second>(First first, Second second) {

	public static <First, Second> Pair2<First, Second> of(First first, Second second) {
		return new Pair2<>(first, second);
	}
}
