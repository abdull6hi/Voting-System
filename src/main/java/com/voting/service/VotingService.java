package com.voting.service;

import com.voting.dao.DatabaseManager;
import com.voting.exception.*;
import java.sql.*;

public class VotingService {
    private final DatabaseManager db;

    public VotingService(DatabaseManager db) {
        this.db = db;
    }

    public void castVote(String electionId, String studentId, String candidateId)
            throws ElectionClosedException, AlreadyVotedException,
            CandidateNotFoundException, SQLException {

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Check election status
            if (!isElectionOpen(conn, electionId)) {
                throw new ElectionClosedException(electionId);
            }

            // 2. Check voting status
            if (hasVoted(conn, studentId)) {
                throw new AlreadyVotedException(); // Using parameterless constructor
            }

            // 3. Validate candidate exists
            if (!candidateExists(conn, candidateId)) {
                throw new CandidateNotFoundException(candidateId);
            }

            // 4. Record vote
            incrementVote(conn, candidateId);
            markAsVoted(conn, studentId);

            conn.commit();
        }
    }

    private boolean isElectionOpen(Connection conn, String electionId) throws SQLException {
        String sql = "SELECT is_open FROM elections WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, electionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean("is_open");
            }
        }
    }

    private boolean hasVoted(Connection conn, String studentId) throws SQLException {
        String sql = "SELECT has_voted FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean("has_voted");
            }
        }
    }

    private boolean candidateExists(Connection conn, String candidateId) throws SQLException {
        String sql = "SELECT 1 FROM candidates WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void incrementVote(Connection conn, String candidateId) throws SQLException {
        String sql = "UPDATE candidates SET votes = votes + 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, candidateId);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Failed to update vote count");
            }
        }
    }

    private void markAsVoted(Connection conn, String studentId) throws SQLException {
        String sql = "UPDATE students SET has_voted = TRUE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Failed to mark student as voted");
            }
        }
    }
}