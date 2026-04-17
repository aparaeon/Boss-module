package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ClassWithMultipleFieldInjections {
	public @Inject ServiceA serviceA;
	public @Inject ServiceB serviceB;
}