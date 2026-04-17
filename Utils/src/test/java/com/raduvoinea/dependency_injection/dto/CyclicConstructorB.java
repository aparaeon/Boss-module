package com.raduvoinea.dependency_injection.dto;

public class CyclicConstructorB {
	public CyclicConstructorB(CyclicConstructorA cyclicA) {
	}
}