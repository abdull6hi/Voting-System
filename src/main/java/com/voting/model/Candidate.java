package com.voting.model;

import java.util.Objects;

/**
 * Represents a candidate in an election.
 * Vote count is mutable but controlled via package-private methods.
 */
public class Candidate {
    private final String id;
    private final String name;
    private final String bio;
    private int votes;

    public Candidate(String id, String name, String bio, int votes) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Invalid candidate ID");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Invalid name");

        this.id = id;
        this.name = name;
        this.bio = bio;
        this.votes = Math.max(0, votes); // Prevent negative votes
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public int getVotes() { return votes; }

    // Vote management (package-private - only services can modify)
    void incrementVotes() { votes++; }
    void resetVotes() { votes = 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id.equals(candidate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("com.voting.model.Candidate[id=%s, name=%s, votes=%d]", id, name, votes);
    }
}