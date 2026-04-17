package com.raduvoinea.lambda;

import com.raduvoinea.utils.lambda.lambda.non_throwing.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LambdaRunnableExecutorTest {

	@Test
	public void testLambdaExecutors() {
		ArgLambda<List<String>> addEmpty = (list) -> list.add("empty");
		ArgsLambda<List<String>, String> add = List::add;

		ReturnLambda<String> getHelloWorld = () -> "Hello World";
		ReturnArgLambda<String, String> getHello = (arg) -> "Hello " + arg;
		Return2ArgsLambda<String, String, String> concatenateStrings = (arg1, arg2) -> arg1 + arg2;

		List<String> list = new ArrayList<>();

		addEmpty.run(list);
		//noinspection ConstantValue
		assertEquals(1, list.size());
		assertEquals("empty", list.getFirst());

		add.run(list, "test");
		assertEquals(2, list.size());
		assertEquals("test", list.get(1));

		assertEquals("Hello World", getHelloWorld.run());
		assertEquals("Hello test", getHello.run("test"));
		assertEquals("testtest", concatenateStrings.run("test", "test"));
	}

	@SneakyThrows
	@Test
	@Disabled
	public void testRunTaskLater() {
		AtomicBoolean executed = new AtomicBoolean(false);

//		ScheduleUtils.runTaskLater(() -> executed.set(true), 1000);

		Thread.sleep(1500);

		assertTrue(executed.get());
	}

	@SneakyThrows
	@Test
	@Disabled
	public void testRunTaskTimer() {
		AtomicInteger executed = new AtomicInteger(0);

//		ScheduleUtils.runTaskTimer(new CancelableTimeTask() {
//			@Override
//			public void execute() {
//				executed.getAndAdd(1);
//
//				if (executed.get() == 5) {
//					this.cancel();
//				}
//			}
//		}, 1000);

		Thread.sleep(7000);

		assertEquals(5, executed.get());
	}

}
