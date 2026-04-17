package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ComplexClass {

	public final ServiceA serviceA;
	public @Inject ServiceC serviceC;

	@Inject
	public ComplexClass(ServiceA serviceA) {
		this.serviceA = serviceA;
	}

}