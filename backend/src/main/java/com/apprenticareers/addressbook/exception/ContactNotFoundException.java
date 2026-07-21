package com.apprenticareers.addressbook.exception;

public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(Long id) {
        super("No contact found with id " + id);
    }

    public ContactNotFoundException(String message) {
        super(message);
    }
}
