package com.raduvoinea.utils.lambda.lambda.non_throwing;

@FunctionalInterface
public interface ReturnLambda<Result> {
	Result run();
}