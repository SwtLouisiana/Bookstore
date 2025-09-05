package com.bookstore.exception;

public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
    
    public RegistrationException(String message, Exception e) {
        super(message);
    }
}
