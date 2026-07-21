package com.apprenticareers.addressbook.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("No user found with id " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
