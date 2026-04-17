package com.raduvoinea.message_builder;


import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageBuilderTests {

	@Test
	public void testMessageBuilder() {
		MessageBuilder builder1 = new MessageBuilder("This is a %placeholder%");
		MessageBuilder builder2 = new MessageBuilder("This is a %placeholder-1% with %placeholder-2%");

		String result1 = builder1
				.parse("placeholder", "banana")
				.parse();

		String result2 = builder2
				.parse("placeholder-1", "banana")
				.parse("%placeholder-2%", "1000 calories")
				.parse();

		assertEquals("This is a banana", result1);
		assertEquals("This is a banana with 1000 calories", result2);
	}

	@Test
	public void testMessageBuilderList() {
		MessageBuilderList builder = new MessageBuilderList(Arrays.asList(
				"This is a %placeholder-1%",
				"This %placeholder-1% has %placeholder-2%"
		));

		List<String> result = builder
				.parse("placeholder-1", "banana")
				.parse("%placeholder-2%", "1000 calories")
				.parse();

		List<String> expected = Arrays.asList(
				"This is a banana",
				"This banana has 1000 calories"
		);
		assertArrayEquals(expected.toArray(), result.toArray());
	}

	@Test
	public void testMultiParse() {
		MessageBuilder testMessage = new MessageBuilder("{p1}")
				.parse("p1", "{p2}")
				.parse("p2", "test2");

		MessageBuilderList testList = new MessageBuilderList(List.of(
				"{p1}",
				"{p2}"
		))

				.parse("p1", "{p3}")
				.parse("p2", "{p4}")
				.parse("p3", "test3")
				.parse("p4", "test4");


		String resultMessage = testMessage.parse();
		List<String> resultList = testList.parse();

		assertEquals("test2", resultMessage);
		assertEquals("test3", resultList.get(0));
		assertEquals("test4", resultList.get(1));
	}

	@Test
	public void testMultiParseReverseOrder() {
		MessageBuilder testMessage = new MessageBuilder("{p1}")
				.parse("p2", "test2")
				.parse("p1", "{p2}");

		MessageBuilderList testList = new MessageBuilderList(List.of(
				"{p1}",
				"{p2}"
		))
				.parse("p3", "test3")
				.parse("p4", "test4")
				.parse("p1", "{p3}")
				.parse("p2", "{p4}");


		String resultMessage = testMessage.parse();
		List<String> resultList = testList.parse();

		assertEquals("test2", resultMessage);
		assertEquals("test3", resultList.get(0));
		assertEquals("test4", resultList.get(1));
	}

	@Test
	public void testSimpleNullParse() {
		MessageBuilder builder = new MessageBuilder("This is a {placeholder-1} {placeholder-2}");

		String result1 = builder
				.parse("placeholder-1", null)
				.parse("placeholder-2", "banana")
				.parse();

		assertEquals("This is a null banana", result1);
	}

	@Test
	public void testMapNullParse() {
		MessageBuilder builder = new MessageBuilder("This is a {placeholder-1} {placeholder-2}");

		Map<String, String> placeholders = new HashMap<>() {{
			put("placeholder-1", "null");
			put("placeholder-2", "banana");
		}};

		String result1 = builder
				.parse(placeholders)
				.parse();

		assertEquals("This is a null banana", result1);
	}

}
