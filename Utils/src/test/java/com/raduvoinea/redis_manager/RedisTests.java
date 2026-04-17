package com.raduvoinea.redis_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.redis_manager.dto.event_serialization.ComplexEvent1;
import com.raduvoinea.redis_manager.dto.event_serialization.SimpleEvent1;
import com.raduvoinea.redis_manager.dto.event_serialization.SimpleEvent2;
import com.raduvoinea.redis_manager.manager.TestListener;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableListGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableMapGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableObjectTypeAdapter;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.message_builder.MessageBuilderManager;
import com.raduvoinea.utils.redis_manager.dto.RedisConfig;
import com.raduvoinea.utils.redis_manager.dto.gson.RedisRequestGsonTypeAdapter;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SetEnvironmentVariable(key = "DEV_HOSTNAME", value = "localhost")
public class RedisTests {

	public static RedisManager REDIS_MANAGER;
	private static final Holder<Gson> gsonHolder = Holder.empty();


	@BeforeAll
	public static void init() {
		MessageBuilderManager.init(false);

		ClassLoader classLoader = RedisTests.class.getClassLoader();
		EventManager eventManager = new EventManager();

		System.out.println(System.getenv("DEV_HOSTNAME"));

		REDIS_MANAGER = new RedisManager(gsonHolder, new RedisConfig(), classLoader, Holder.of(eventManager), true, true);

		RedisRequestGsonTypeAdapter redisRequestTypeAdapter = new RedisRequestGsonTypeAdapter(classLoader, REDIS_MANAGER);
		SerializableListGsonTypeAdapter serializableListGsonTypeAdapter = new SerializableListGsonTypeAdapter(classLoader);
		SerializableMapGsonTypeAdapter serializableMapGsonTypeAdapter = new SerializableMapGsonTypeAdapter(classLoader);
		SerializableObjectTypeAdapter serializableObjectTypeAdapter = new SerializableObjectTypeAdapter(classLoader);


		GsonBuilder gsonBuilder = new GsonBuilder()
				.registerTypeAdapter(redisRequestTypeAdapter.getSerializedClass(), redisRequestTypeAdapter)
				.registerTypeAdapter(serializableListGsonTypeAdapter.getSerializedClass(), serializableListGsonTypeAdapter)
				.registerTypeAdapter(serializableMapGsonTypeAdapter.getSerializedClass(), serializableMapGsonTypeAdapter)
				.registerTypeAdapter(serializableObjectTypeAdapter.getSerializedClass(), serializableObjectTypeAdapter);

		Gson gson = gsonBuilder.create();
		gsonHolder.set(gson);

		REDIS_MANAGER.getEventManagerHolder().value().register(TestListener.class);
	}

	@Test
	@SneakyThrows
	public void simpleEvent1() {
		SimpleEvent1 event = new SimpleEvent1(10, 20);
		CompletableFuture<Integer> future = event.send();
		Integer result = future.get();

		assertEquals(Future.State.SUCCESS, future.state());
		assertEquals(30, result);
	}

	@Test
	@SneakyThrows
	public void simpleEvent2() {
		SimpleEvent2 event = new SimpleEvent2(Arrays.asList("test1", "test2"), "-");
		CompletableFuture<String> future = event.send();
		String result = future.get();

		assertEquals(Future.State.SUCCESS, future.state());
		assertEquals("test1-test2-", result);
	}

	@Test
	@SneakyThrows
	public void complexEvent1() {
		ComplexEvent1 event = new ComplexEvent1(Arrays.asList("test1", "test2"), "test3");
		CompletableFuture<List<String>> future = event.send();
		List<String> result = future.get();

		assertEquals(Future.State.SUCCESS, future.state());
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("test1", result.get(0));
		assertEquals("test2", result.get(1));
		assertEquals("test3", result.get(2));
	}

//	@Test
//	public void testGsonImplementation1() {
//		RedisRequest<Boolean> event1 = new RedisRequest<>("test") {
//			@Override
//			public RedisManager getRedisManager() {
//				return REDIS_MANAGER;
//			}
//		};
//		event1.setId(100);
//		event1.setOriginator("test_env");
//
//		String json = REDIS_MANAGER.getGsonHolder().value().toJson(event1);
//
//		RedisRequest<?> event2 = REDIS_MANAGER.getGsonHolder().value().fromJson(json, RedisRequest.class);
//
//		assertNotNull(event2);
//		assertEquals(event1.getClassName(), event2.getClassName());
//		assertEquals(event1.getId(), event2.getId());
//		assertEquals(event1.getOriginator(), event2.getOriginator());
//		assertEquals(event1.getTarget(), event2.getTarget());
//	}

	@Test
	public void testGsonImplementation2() {
		ComplexEvent1 event1 = new ComplexEvent1(Arrays.asList("test1", "test2"), "test3");
		event1.setId(100);
		event1.setOriginator("test_env");

		String json = REDIS_MANAGER.getGsonHolder().value().toJson(event1);

		RedisRequest<?> event2 = REDIS_MANAGER.getGsonHolder().value().fromJson(json, RedisRequest.class);

		assertNotNull(event2);
		assertEquals(event1.getClassName(), event2.getClassName());
		assertEquals(event1.getId(), event2.getId());
		assertEquals(event1.getOriginator(), event2.getOriginator());
		assertEquals(event1.getTarget(), event2.getTarget());
		assertInstanceOf(ComplexEvent1.class, event2);

		ComplexEvent1 event3 = (ComplexEvent1) event2;

		assertNotNull(event3);
		assertEquals(event1.getA(), event3.getA());
		assertEquals(event1.getB(), event3.getB());
	}

}
