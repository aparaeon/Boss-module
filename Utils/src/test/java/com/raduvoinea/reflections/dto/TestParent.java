package com.raduvoinea.reflections.dto;

import com.raduvoinea.reflections.annotation.TestAnnotation;

@SuppressWarnings("unused")
@TestAnnotation
public class TestParent {

	public int publicParentField;
	protected int protectedParentField;
	private int privateParentField;

	@TestAnnotation
	public void testMethod() {

	}

}
