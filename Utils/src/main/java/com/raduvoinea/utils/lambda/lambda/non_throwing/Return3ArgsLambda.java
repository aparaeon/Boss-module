package com.raduvoinea.utils.lambda.lambda.non_throwing;

@FunctionalInterface
public interface Return3ArgsLambda<Result, FirstArgument, SecondArgument, ThirdArgument> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument);
}