package org.budget.tracker.gateway.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
    }

    @Override
    public String getMessage() {
        return "Invalid Credentials";
    }
}
