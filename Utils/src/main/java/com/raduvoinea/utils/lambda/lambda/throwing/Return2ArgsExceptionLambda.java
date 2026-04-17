package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface Return2ArgsExceptionLambda<Result, FirstArgument, SecondArgument, ThrownException extends Exception> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument) throws ThrownException;
}