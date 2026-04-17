package com.raduvoinea.file_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.file_manager.dto.files.DefaultFileObject;
import com.raduvoinea.file_manager.dto.files.FileObject;
import com.raduvoinea.file_manager.dto.files.ResourceFileObject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.dto.Holder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileManagerTests {

	private static final String TEST_1 = "test1";

	private static FileManager fileManager;
	private static Holder<Gson> gsonHolder;

	@BeforeAll
	public static void init() {
		Gson gson = new GsonBuilder().create();
		gsonHolder = Holder.of(gson);
		fileManager = new FileManager(gsonHolder, "tmp");
	}

	@AfterAll
	public static void cleanup() {
		deleteDirectory(fileManager.getDataFolder());
	}

	private static void deleteDirectory(File dir) {
		File[] allContents = dir.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}

		//noinspection ResultOfMethodCallIgnored
		dir.delete();
	}

	@Test
	public void testObjectSaveLoad() {
		FileObject object = new FileObject(101, TEST_1);

		fileManager.save(object);

		FileObject loadedObject = fileManager.load(FileObject.class);

		assertEquals(object.data1, loadedObject.data1);
		assertEquals(object.data2, loadedObject.data2);
	}

	@Test
	public void testDefaultObject(){
		DefaultFileObject object = fileManager.load(DefaultFileObject.class);

		assertEquals(object.data1, 123);
		assertEquals(object.data2, "123");
	}

	@Test
	public void testResourceObject(){
		ResourceFileObject object = fileManager.load(ResourceFileObject.class);

		assertEquals(object.data1, 12345);
		assertEquals(object.data2, "12345");
	}
}
