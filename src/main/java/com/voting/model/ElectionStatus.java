package com.voting.model;

import java.time.LocalDateTime;

public class ElectionStatus {
    private final boolean isOpen;
    private final LocalDateTime openedAt;
    private final LocalDateTime closedAt;
    private final int totalVotes;
    private final int totalVoters;
    private final int votedCount;

    public ElectionStatus(boolean isOpen,
                          LocalDateTime openedAt,
                          LocalDateTime closedAt,
                          int totalVotes,
                          int totalVoters,
                          int votedCount) {
        this.isOpen = isOpen;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.totalVotes = totalVotes;
        this.totalVoters = totalVoters;
        this.votedCount = votedCount;
    }

    // Getters
    public boolean isOpen() { return isOpen; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public int getTotalVotes() { return totalVotes; }
    public int getTotalVoters() { return totalVoters; }
    public int getVotedCount() { return votedCount; }
    public double getParticipationRate() {
        return totalVoters > 0 ? (votedCount * 100.0) / totalVoters : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Status: %s | Opened: %s | Closed: %s | Votes: %d | Participation: %.1f%%",
                isOpen ? "OPEN" : "CLOSED",
                openedAt != null ? openedAt : "N/A",
                closedAt != null ? closedAt : "N/A",
                totalVotes,
                getParticipationRate()
        );
    }
}