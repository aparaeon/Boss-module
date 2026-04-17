package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ClassWithMultipleConstructors {

	public final ServiceA serviceA;

	public ClassWithMultipleConstructors() {
		this.serviceA = null;
	}

	@Inject
	public ClassWithMultipleConstructors(ServiceA serviceA) {
		this.serviceA = serviceA;
	}

	public ClassWithMultipleConstructors(ServiceA serviceA, ServiceC serviceC) {
		this.serviceA = serviceA;
	}

}