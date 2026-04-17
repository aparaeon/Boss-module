package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ClassWithFieldInjection {

	public @Inject ServiceA serviceA;

	public ClassWithFieldInjection() {
	}

}