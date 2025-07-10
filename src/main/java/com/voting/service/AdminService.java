package com.voting.service;

import com.voting.dao.DatabaseManager;
import com.voting.exception.VotingException;
import com.voting.model.Candidate;
import com.voting.model.ElectionStatus;

import java.sql.*;
import java.time.LocalDateTime;

public class AdminService {
    private final DatabaseManager db;

    public AdminService(DatabaseManager db) {
        this.db = db;
    }

    public boolean isElectionOpen() throws SQLException {
        try (Connection conn = db.getConnection()) {
            return isElectionOpen(conn);
        }
    }

    public boolean isElectionOpen(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT is_open FROM elections WHERE id = 1");
            return rs.next() && rs.getBoolean("is_open");
        }
    }

    public void openElection() throws SQLException, VotingException {
        db.runTransaction(conn -> {
            if (isElectionOpen(conn)) {
                throw new VotingException("Election is already open");
            }
            updateElectionStatus(conn, true);
        });
    }

    public void closeElection() throws SQLException, VotingException {
        db.runTransaction(conn -> {
            if (!isElectionOpen(conn)) {
                throw new VotingException("No active election to close");
            }
            updateElectionStatus(conn, false);
        });
    }

    private void updateElectionStatus(Connection conn, boolean isOpen) throws SQLException {
        String sql = isOpen ?
                "UPDATE elections SET is_open = TRUE, opened_at = CURRENT_TIMESTAMP, closed_at = NULL WHERE id = 1" :
                "UPDATE elections SET is_open = FALSE, closed_at = CURRENT_TIMESTAMP WHERE id = 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Failed to update election status");
            }
        }
    }

    public void resetAllVotes() throws SQLException, VotingException {
        db.runTransaction(conn -> {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE candidates SET votes = 0");
                stmt.executeUpdate("UPDATE students SET has_voted = FALSE");
                stmt.executeUpdate("UPDATE elections SET is_open = FALSE, opened_at = NULL, closed_at = NULL WHERE id = 1");
            }
        });
    }

    public ElectionStatus getElectionStatus() throws SQLException {
        try (Connection conn = db.getConnection()) {
            // Get election info
            String electionSql = "SELECT is_open, opened_at, closed_at FROM elections WHERE id = 1";
            boolean isOpen = false;
            LocalDateTime openedAt = null;
            LocalDateTime closedAt = null;

            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(electionSql);
                if (rs.next()) {
                    isOpen = rs.getBoolean("is_open");
                    openedAt = rs.getTimestamp("opened_at") != null ?
                            rs.getTimestamp("opened_at").toLocalDateTime() : null;
                    closedAt = rs.getTimestamp("closed_at") != null ?
                            rs.getTimestamp("closed_at").toLocalDateTime() : null;
                }
            }


            // Get vote stats
            int totalVotes = 0;
            int totalVoters = 0;
            int votedCount = 0;

            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT SUM(votes) FROM candidates");
                if (rs.next()) totalVotes = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
                if (rs.next()) totalVoters = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM students WHERE has_voted = TRUE");
                if (rs.next()) votedCount = rs.getInt(1);
            }

            return new ElectionStatus(isOpen, openedAt, closedAt, totalVotes, totalVoters, votedCount);
        }
    }

    // Candidate management methods
    public void addCandidate(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO candidates (id, name, bio) VALUES (?, ?, ?)";
        db.executeUpdate(sql, candidate.getId(), candidate.getName(), candidate.getBio());
    }

    public void removeCandidate(String candidateId) throws SQLException, VotingException {
        String sql = "DELETE FROM candidates WHERE id = ?";
        int affectedRows = db.executeUpdate(sql, candidateId);

        if (affectedRows == 0) {
            throw new VotingException("Candidate not found");
        }
    }
}