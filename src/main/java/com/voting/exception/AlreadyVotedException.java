package com.voting.exception;

import java.time.LocalDateTime;

public class AlreadyVotedException extends VotingException {
  private final LocalDateTime voteTime;

  // Add parameterless constructor
  public AlreadyVotedException() {
    this(LocalDateTime.now());
  }

  public AlreadyVotedException(LocalDateTime voteTime) {
    super("You already voted on " + voteTime);
    this.voteTime = voteTime;
  }

  public LocalDateTime getVoteTime() {
    return voteTime;
  }
}