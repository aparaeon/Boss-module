package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class CyclicConstructorA {
	@Inject
	public CyclicConstructorA(CyclicConstructorB cyclicB) {
	}
}