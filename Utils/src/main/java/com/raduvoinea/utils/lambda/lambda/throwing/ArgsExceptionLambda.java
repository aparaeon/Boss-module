package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface ArgsExceptionLambda<FirstArgument, SecondArgument, ThrownException extends Exception> {
	void run(FirstArgument firstArgument, SecondArgument secondArgument)throws ThrownException;
}