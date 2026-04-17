package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface ArgExceptionLambda<Argument, ThrownException extends Exception> {
	void run(Argument argument) throws ThrownException;
}