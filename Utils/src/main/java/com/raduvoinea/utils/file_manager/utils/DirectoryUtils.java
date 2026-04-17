package com.raduvoinea.utils.file_manager.utils;

import java.io.File;

public class DirectoryUtils {

	public static void deleteRecursively(File file) {
		if (!file.isDirectory()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
			return;
		}

		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteRecursively(f);
			}
		}
	}

}
