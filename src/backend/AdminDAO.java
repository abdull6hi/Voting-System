package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // Check if a user is an admin by admission number
    public static boolean isAdmin(String admissionNumber) throws SQLException {
        String sql = "SELECT role FROM tbl_users WHERE admission_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admissionNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "admin".equalsIgnoreCase(rs.getString("role"));
                }
            }
        }
        return false;
    }

    // Promote a user to candidate
    public static boolean promoteToCandidate(String admissionNumber) throws SQLException {
        String sql = "UPDATE tbl_users SET role = 'candidate' WHERE admission_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admissionNumber);
            return stmt.executeUpdate() == 1;
        }
    }

    // Demote a candidate back to regular user
    public static boolean demoteCandidate(String admissionNumber) throws SQLException {
        String sql = "UPDATE tbl_users SET role = 'user' WHERE admission_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admissionNumber);
            return stmt.executeUpdate() == 1;
        }
    }
}
