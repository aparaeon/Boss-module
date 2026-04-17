package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class ClassWithMixedFields {

	public @Inject ServiceA injectedField;
	public ServiceA nonInjectedField;

	public ClassWithMixedFields() {
	}

}