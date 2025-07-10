package com.voting.service;

import com.voting.dao.DatabaseManager;
import com.voting.exception.AuthException;
import com.voting.model.Student;
import com.voting.util.PasswordHasher;
import java.sql.*;

public class AuthService {
    private final DatabaseManager db;

    public AuthService(DatabaseManager db) {
        this.db = db;
    }

    public Student login(String username, String password) throws AuthException {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            throw new AuthException("Username cannot be empty");
        }

        String sql = "SELECT id, name, password_hash, has_voted FROM students WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new AuthException("Invalid username or password");
            }

            String storedHash = rs.getString("password_hash");
            if (storedHash == null || storedHash.isEmpty()) {
                throw new AuthException("System configuration error");
            }

            if (!PasswordHasher.verify(password, storedHash)) {
                throw new AuthException("Invalid username or password");
            }

            return new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getBoolean("has_voted")
            );

        } catch (SQLException e) {
            System.err.println("Database error during login:");
            e.printStackTrace();
            throw new AuthException("System error. Please try again later");
        }
    }

    public void registerStudent(String studentId, String name, String password)
            throws AuthException, SQLException {

        if (studentId == null || studentId.isBlank() ||
                name == null || name.isBlank() ||
                password == null || password.isBlank()) {
            throw new AuthException("All fields are required");
        }

        String hashedPassword = PasswordHasher.hash(password);
        String sql = "INSERT INTO students (id, name, password_hash) VALUES (?, ?, ?)";

        try {
            db.executeUpdate(sql, studentId, name, hashedPassword);
        } catch (SQLException e) {
            throw new AuthException("Registration failed: " + e.getMessage());
        }
    }
}