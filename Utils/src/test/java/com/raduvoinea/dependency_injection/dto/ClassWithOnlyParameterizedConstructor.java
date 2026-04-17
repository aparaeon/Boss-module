package com.raduvoinea.dependency_injection.dto;

public class ClassWithOnlyParameterizedConstructor {

	public final ServiceA serviceA;

	public ClassWithOnlyParameterizedConstructor(ServiceA serviceA) {
		this.serviceA = serviceA;
	}

}