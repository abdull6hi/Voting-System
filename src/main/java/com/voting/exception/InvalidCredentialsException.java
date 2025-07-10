package com.voting.exception;

/**
 * Thrown when authentication fails due to invalid username/password
 * or non-existent user.
 */
public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }

    public InvalidCredentialsException(Throwable cause) {
        super("Invalid username or password", cause);
    }
}