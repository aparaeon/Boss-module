package com.raduvoinea.utils.message_builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MessageBuilderList extends GenericMessageBuilder<List<String>> {

	/**
	 * Can use the separator %%new-line%% to split the message into multiple lines.
	 *
	 * @param base The base message to use.
	 */
	public MessageBuilderList(List<String> base) {
		super(base);
	}

	public MessageBuilderList(List<String> base, List<Object> placeholders, List<Object> values) {
		super(base, placeholders, values);
	}

	@Override
	protected boolean equals(List<String> o1, List<String> o2) {
		boolean output = true;

		if (o1 == null) {
			return o2 == null;
		}

		if (o1.size() != o2.size()) {
			return false;
		}

		for (int i = 0; i < o1.size(); i++) {
			output = output && o1.get(i).equals(o2.get(i));
		}

		return output;
	}

	@Override
	protected String convertToString() {
		List<String> parsed = parse();
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < parsed.size() - 1; i++) {
			output
					.append(parsed.get(i))
					.append('\n');
		}

		output.append(parsed.getLast());

		return output.toString();
	}

	@Override
	protected List<String> parsePlaceholder(List<String> base, String placeholder, String value) {
		if (base == null) {
			return new ArrayList<>();
		}

		List<String> output = new ArrayList<>();

		for (String line : base) {
			output.add(line.replace(placeholder, value));
		}

		return output;
	}

	@Override
	protected List<String> parsePlaceholder(List<String> base, String placeholder, List<String> values) {
		if (base == null) {
			return new ArrayList<>();
		}

		String value = convertListToString(values);

		List<String> output = new ArrayList<>();

		for (String line : base) {
			if (line.equals(placeholder)) {
				output.addAll(values);
				continue;
			}

			output.add(line.replace(placeholder, value));
		}

		return output;
	}

	@Override
	public MessageBuilderList clone() {
		return new MessageBuilderList(base, new ArrayList<>(placeholders), new ArrayList<>(values));
	}

	public MessageBuilderList parse(Map<?, ?> placeholders) {
		return (MessageBuilderList) super.parse(placeholders);
	}

	public MessageBuilderList parse(Object placeholder, Object value) {
		return (MessageBuilderList) super.parse(placeholder, value);
	}
}
