package com.raduvoinea.dependency_injection.dto;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;

public class CyclicA {
	public @Inject CyclicB cyclicB;
}
