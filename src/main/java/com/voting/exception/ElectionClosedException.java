package com.voting.exception;

/**
 * Thrown when voting is attempted on a closed election.
 */
public class ElectionClosedException extends VotingException {
    private final String electionId;

    public ElectionClosedException(String electionId) {
        super("Election " + electionId + " is not active");
        this.electionId = electionId;
    }

    public String getElectionId() {
        return electionId;
    }
}