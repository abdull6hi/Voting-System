package com.voting.exception;

/**
 * Thrown during authentication failures
 * (e.g., invalid credentials, user not found)
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}