package com.raduvoinea.utils.generic.dto;

public record Pair3<First, Second, Third>(First first, Second second, Third third) {

	public static <First, Second, Third> Pair3<First, Second, Third> of(First first, Second second, Third third) {
		return new Pair3<>(first, second, third);
	}

}
