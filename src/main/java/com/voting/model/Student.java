package com.voting.model;

public class Student {
    private final String id;
    private final String name;
    private final String passwordHash;
    private boolean hasVoted;

    // 4-arg constructor (original)
    public Student(String id, String name, String passwordHash, boolean hasVoted) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.hasVoted = hasVoted;
    }

    // New 3-arg constructor
    public Student(String id, String name, boolean hasVoted) {
        this(id, name, "", hasVoted);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPasswordHash() { return passwordHash; }
    public boolean hasVoted() { return hasVoted; }
}