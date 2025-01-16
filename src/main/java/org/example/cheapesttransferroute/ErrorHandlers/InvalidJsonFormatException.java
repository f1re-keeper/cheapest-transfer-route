package org.example.cheapesttransferroute.ErrorHandlers;

public class InvalidJsonFormatException extends RuntimeException {
    public InvalidJsonFormatException(String message) {
        super(message);
    }
}
