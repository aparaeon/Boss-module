package com.raduvoinea.utils.lambda.lambda.throwing;


@FunctionalInterface
public interface ReturnArgExceptionLambda<Result, Argument, ThrownException extends Exception> {
	Result run(Argument argument) throws ThrownException;
}