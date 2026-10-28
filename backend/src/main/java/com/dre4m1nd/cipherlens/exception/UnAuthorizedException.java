package com.dre4m1nd.cipherlens.exception;

/**
 * @author dre4m1nd
 */
public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException(String message) {
        super(message);
    }
}
