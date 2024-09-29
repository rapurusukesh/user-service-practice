package com.hire10x.createuser.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {

        super(message);
    }
}

