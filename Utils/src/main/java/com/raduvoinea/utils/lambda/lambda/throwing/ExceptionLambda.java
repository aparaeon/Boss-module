package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface ExceptionLambda<ThrownException extends Exception>{
	void run() throws ThrownException;
}
