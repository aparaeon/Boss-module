package com.raduvoinea.utils.lambda.lambda.non_throwing;

@FunctionalInterface
public interface ArgsLambda<FirstArgument, SecondArgument> {
	void run(FirstArgument firstArgument, SecondArgument secondArgument);
}