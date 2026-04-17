package com.raduvoinea.utils.lambda.lambda.non_throwing;


@FunctionalInterface
public interface ReturnArgLambda<Result, Argument> {
	Result run(Argument argument);
}