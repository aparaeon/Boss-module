package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class CyclicC {
	public @Inject CyclicA cyclicA;

	public @Inject CyclicB cyclicB;
}