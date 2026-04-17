package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface ReturnExceptionLambda<Result, ThrownException extends Exception> {
	Result run() throws ThrownException;
}