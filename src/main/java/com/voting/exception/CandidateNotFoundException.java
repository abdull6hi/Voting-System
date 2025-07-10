package com.voting.exception;

/**
 * Thrown when operations reference a candidate that doesn't exist
 */
public class CandidateNotFoundException extends VotingException {
    private final String candidateId;

    public CandidateNotFoundException(String candidateId) {
        super("Candidate '" + candidateId + "' not found");
        this.candidateId = candidateId;
    }

    public String getCandidateId() {
        return candidateId;
    }
}