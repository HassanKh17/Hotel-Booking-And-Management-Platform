package com.hotel.management.system.exception;

public class IllegalUsernameException extends RuntimeException {
    public static final String USERNAME_EXISTS = "Username already exists";

    public IllegalUsernameException(String message) {
        super(message);
    }
}
