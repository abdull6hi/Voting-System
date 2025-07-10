package com.voting.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an election event with time-bound voting.
 * Enforces state transitions (DRAFT → OPEN → CLOSED).
 */
public class Election {
    private final String id;
    private final String title;
    private boolean isOpen;
    private final LocalDateTime createdAt;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private final List<Candidate> candidates;

    public Election(String id, String title, List<Candidate> candidates) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Invalid election ID");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Invalid title");

        this.id = id;
        this.title = title;
        this.candidates = List.copyOf(candidates); // Defensive copy
        this.createdAt = LocalDateTime.now();
        this.isOpen = false;
    }

    // State management
    public void open() {
        if (isOpen) throw new IllegalStateException("com.voting.model.Election already open");
        if (closedAt != null) throw new IllegalStateException("Closed elections cannot reopen");

        this.isOpen = true;
        this.openedAt = LocalDateTime.now();
    }

    public void close() {
        if (!isOpen) throw new IllegalStateException("com.voting.model.Election is not open");
        this.isOpen = false;
        this.closedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isOpen() { return isOpen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public List<Candidate> getCandidates() { return List.copyOf(candidates); }

    @Override
    public String toString() {
        return String.format(
                "com.voting.model.Election[id=%s, title=%s, status=%s]",
                id,
                title,
                isOpen ? "OPEN" : (closedAt != null ? "CLOSED" : "DRAFT")
        );
    }
}