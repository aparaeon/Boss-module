package com.raduvoinea.dependency_injection;

import com.raduvoinea.dependency_injection.dto.*;
import com.raduvoinea.utils.dependency_injection.Injector;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InjectorTest {

	private Injector injector;

	@BeforeEach
	void setUp() {
		injector = new Injector();
	}

	@Test
	void testBind() {
		ServiceA serviceA = new ServiceA();
		ServiceA result = injector.bind(ServiceA.class, serviceA);

		assertSame(serviceA, result);
		assertTrue(injector.getDependencies().containsKey(ServiceA.class));
		assertSame(serviceA, injector.getDependencies().get(ServiceA.class));
	}

	@Test
	void testBindWithInheritance() {
		ServiceB serviceB = new ServiceB();
		ServiceB result = injector.bind(ServiceA.class, serviceB);

		assertSame(serviceB, result);
		assertSame(serviceB, injector.getDependencies().get(ServiceA.class));
	}


	@Test
	void testInject() throws InjectionException {
		ServiceA serviceA = new ServiceA();
		injector.bind(ServiceA.class, serviceA);

		ClassWithFieldInjection instance = new ClassWithFieldInjection();
		injector.inject(instance);

		assertSame(serviceA, instance.serviceA);
	}

	@Test
	void testInjectWithMissingDependency() {
		ClassWithFieldInjection instance = new ClassWithFieldInjection();

		InjectionException exception = assertThrows(
				InjectionException.class,
				() -> injector.inject(instance, false)
		);

		assertTrue(exception.getMessage().contains("Missing dependency"));
	}

	@Test
	void testInjectSkipsNonAnnotatedFields() throws InjectionException {
		ClassWithMixedFields instance = new ClassWithMixedFields();
		ServiceA serviceA = new ServiceA();
		injector.bind(ServiceA.class, serviceA);

		injector.inject(instance);

		assertSame(serviceA, instance.injectedField);
		assertNull(instance.nonInjectedField);
	}


	@Test
	@DisplayName("resolveUnresolved should resolve deferred dependencies")
	void testResolveUnresolvedDependencies() throws InjectionException {
		ServiceA serviceA = new ServiceA();
		ClassWithFieldInjection instance = new ClassWithFieldInjection();

		injector.inject(instance, true);
		assertEquals(1, injector.getUnresolvedDependencies().size());
		assertNull(instance.serviceA);

		injector.bind(ServiceA.class, serviceA);
		int resolved = injector.resolveUnresolved();

		assertEquals(1, resolved);
		assertEquals(0, injector.getUnresolvedDependencies().size());
		assertSame(serviceA, instance.serviceA);
	}

	@Test
	@DisplayName("resolveUnresolved should handle multiple unresolved dependencies")
	void testResolveMultipleUnresolved() throws InjectionException {
		ClassWithMultipleFieldInjections instance = new ClassWithMultipleFieldInjections();

		injector.inject(instance, true);
		assertEquals(2, injector.getUnresolvedDependencies().size());

		ServiceA serviceA = new ServiceA();
		ServiceB serviceB = new ServiceB();
		injector.bind(ServiceA.class, serviceA);
		injector.bind(ServiceB.class, serviceB);

		int resolved = injector.resolveUnresolved();

		assertEquals(2, resolved);
		assertEquals(0, injector.getUnresolvedDependencies().size());
		assertSame(serviceA, instance.serviceA);
		assertSame(serviceB, instance.serviceB);
	}

	@Test
	@DisplayName("resolveUnresolved should partially resolve when some dependencies available")
	void testPartialResolve() throws InjectionException {
		ClassWithMultipleFieldInjections instance = new ClassWithMultipleFieldInjections();

		injector.inject(instance, true);
		assertEquals(2, injector.getUnresolvedDependencies().size());

		ServiceA serviceA = new ServiceA();
		injector.bind(ServiceA.class, serviceA);

		int resolved = injector.resolveUnresolved();

		assertEquals(1, resolved);
		assertEquals(1, injector.getUnresolvedDependencies().size());
		assertSame(serviceA, instance.serviceA);
		assertNull(instance.serviceB);
	}

	@Test
	void testClearUnresolved() throws InjectionException {
		ClassWithFieldInjection instance = new ClassWithFieldInjection();
		injector.inject(instance, true);

		assertEquals(1, injector.getUnresolvedDependencies().size());

		injector.clearUnresolved();

		assertEquals(0, injector.getUnresolvedDependencies().size());
	}

	@Test
	void testMultipleObjectsWithUnresolvedDependencies() throws InjectionException {
		ClassWithFieldInjection instance1 = new ClassWithFieldInjection();
		ClassWithFieldInjection instance2 = new ClassWithFieldInjection();

		injector.inject(instance1, true);
		injector.inject(instance2, true);

		assertEquals(2, injector.getUnresolvedDependencies().size());

		ServiceA serviceA = new ServiceA();
		injector.bind(ServiceA.class, serviceA);
		int resolved = injector.resolveUnresolved();

		assertEquals(2, resolved);
		assertSame(serviceA, instance1.serviceA);
		assertSame(serviceA, instance2.serviceA);
	}

	@Test
	void testResolveUnresolvedWithNoDependencies() throws InjectionException {
		ClassWithFieldInjection instance = new ClassWithFieldInjection();
		injector.inject(instance, true);

		int resolved = injector.resolveUnresolved();

		assertEquals(0, resolved);
		assertEquals(1, injector.getUnresolvedDependencies().size());
	}

	@Test
	void testGetUnresolvedDependenciesIsUnmodifiable() throws InjectionException {
		ClassWithFieldInjection instance = new ClassWithFieldInjection();
		injector.inject(instance, true);

		var unresolvedList = injector.getUnresolvedDependencies();

		assertThrows(UnsupportedOperationException.class, unresolvedList::clear);
	}

	@Test
	void testUnresolvedDependencyDetails() throws InjectionException {
		ClassWithFieldInjection instance = new ClassWithFieldInjection();
		injector.inject(instance, true);

		var unresolved = injector.getUnresolvedDependencies().get(0);

		assertSame(instance, unresolved.getTarget());
		assertEquals(ServiceA.class, unresolved.getDependencyType());
		assertEquals("serviceA", unresolved.getField().getName());
	}


}
