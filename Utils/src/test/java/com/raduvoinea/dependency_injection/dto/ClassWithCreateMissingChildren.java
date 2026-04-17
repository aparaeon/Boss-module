package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ClassWithCreateMissingChildren {

	public final ServiceA serviceA;

	@Inject(createMissingChildren = true)
	public ClassWithCreateMissingChildren(ServiceA serviceA) {
		this.serviceA = serviceA;
	}

}