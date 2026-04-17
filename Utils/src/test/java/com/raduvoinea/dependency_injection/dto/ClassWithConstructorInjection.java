package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ClassWithConstructorInjection {

	public final ServiceA serviceA;

	@Inject
	public ClassWithConstructorInjection(ServiceA serviceA) {
		this.serviceA = serviceA;
	}

}