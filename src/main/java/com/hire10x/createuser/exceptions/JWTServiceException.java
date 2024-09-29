package com.hire10x.createuser.exceptions;
public class JWTServiceException extends Exception {
    public JWTServiceException(String message) {
        super(message);
    }

    public JWTServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

