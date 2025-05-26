package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Login user by admission number and password
    public static User loginUser(String admissionNumber, String password) throws SQLException {
        String sql = "SELECT full_name, admission_number, role FROM tbl_users WHERE admission_number = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, admissionNumber);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("full_name");
                    String role = rs.getString("role");
                    return new User(admissionNumber, name, role);
                } else {
                    return null; // Login failed
                }
            }
        }
    }

    // Register a new user, returns true if success, false if admission exists
    public static boolean registerUser(String fullName, String admissionNumber, String password) {
        String checkSql = "SELECT admission_number FROM tbl_users WHERE admission_number = ?";
        String insertSql = "INSERT INTO tbl_users(full_name, admission_number, password, role) VALUES (?, ?, ?, 'student')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, admissionNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false; // Admission number already exists
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, fullName);
                insertStmt.setString(2, admissionNumber);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all candidates as a list of User objects
    public static List<User> getCandidates() throws SQLException {
        List<User> candidates = new ArrayList<>();
        String sql = "SELECT full_name, admission_number, role FROM tbl_users WHERE role = 'candidate'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("full_name");
                String admission = rs.getString("admission_number");
                String role = rs.getString("role");
                candidates.add(new User(admission, name, role));
            }
        }
        return candidates;
    }
}
