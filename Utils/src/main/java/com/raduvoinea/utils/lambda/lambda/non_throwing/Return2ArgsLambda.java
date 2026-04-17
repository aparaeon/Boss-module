package com.raduvoinea.utils.lambda.lambda.non_throwing;

@FunctionalInterface
public interface Return2ArgsLambda<Result, FirstArgument, SecondArgument> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument);
}