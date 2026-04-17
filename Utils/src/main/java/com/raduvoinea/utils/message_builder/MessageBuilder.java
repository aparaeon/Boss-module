package com.raduvoinea.utils.message_builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MessageBuilder extends GenericMessageBuilder<String> {

	public MessageBuilder(String base) {
		super(base);
	}

	protected MessageBuilder(String base, List<Object> placeholders, List<Object> values) {
		super(base, placeholders, values);
	}

	@Override
	protected boolean equals(String o1, String o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	@Override
	protected String convertToString() {
		return parse();
	}

	@Override
	protected String parsePlaceholder(String base, String placeholder, String value) {
		if (base == null) {
			return null;
		}
		return base.replace(placeholder, value);
	}

	@Override
	protected String parsePlaceholder(String base, String placeholder, List<String> values) {
		if (base == null) {
			return null;
		}

		return base.replace(placeholder, convertListToString(values));
	}

	@Override
	public GenericMessageBuilder<String> clone() {
		return new MessageBuilder(base, new ArrayList<>(placeholders), new ArrayList<>(values));
	}

	public MessageBuilder parse(Map<?, ?> placeholders) {
		return (MessageBuilder) super.parse(placeholders);
	}

	public MessageBuilder parse(Object placeholder, Object value) {
		return (MessageBuilder) super.parse(placeholder, value);
	}
}
