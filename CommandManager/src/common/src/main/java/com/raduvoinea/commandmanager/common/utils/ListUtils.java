package com.raduvoinea.commandmanager.common.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {


	public static List<String> getListThatStartsWith(List<String> list, String prefix) {
		return list.stream().filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
	}

}
