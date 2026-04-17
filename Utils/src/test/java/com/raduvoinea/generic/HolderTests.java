package com.raduvoinea.generic;

import com.raduvoinea.utils.generic.dto.Holder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HolderTests {

	@Test
	public void testSimpleHolder() {
		Holder<String> holder = Holder.of("test");
		assertEquals("test", holder.value());

		holder = holder.set("test2");
		assertEquals("test2", holder.value());
	}

	@Test
	public void testReferenceHolder() {
		Holder<String> holder = Holder.of("test");
		assertEquals("test", holder.value());

		HolderHolder holderHolder = new HolderHolder(holder);
		assertEquals("test", holderHolder.holder.value());

		holder = holder.set("test2");
		assertEquals("test2", holder.value());
		assertEquals("test2", holderHolder.holder.value());
	}

	@AllArgsConstructor
	@Getter
	private static class HolderHolder {
		private final Holder<String> holder;
	}


}
