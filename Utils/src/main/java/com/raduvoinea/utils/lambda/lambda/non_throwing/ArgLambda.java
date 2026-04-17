package com.raduvoinea.utils.lambda.lambda.non_throwing;

@FunctionalInterface
public interface ArgLambda<Argument> {
	void run(Argument argument);
}