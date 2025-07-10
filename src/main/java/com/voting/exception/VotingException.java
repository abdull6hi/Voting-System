package com.voting.exception;

/**
 * Thrown when voting-related business rules are violated
 * (e.g., already voted, election closed)
 */
public class VotingException extends Exception {
    public VotingException(String message) {
        super(message);
    }

    public VotingException(String message, Throwable cause) {
        super(message, cause);
    }
}