package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface Return3ArgsExceptionLambda<Result, FirstArgument, SecondArgument, ThirdArgument, ThrownException extends Exception> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument) throws ThrownException;
}