package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static boolean registerUser(String fullName, int admissionNumber, String password) {
        String sql = "INSERT INTO tbl_users (full_name, admission_number, password, role) VALUES (?, ?, ?, 'student')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setInt(2, admissionNumber);
            stmt.setString(3, password);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginUser(int admissionNumber, String password) {
        String sql = "SELECT id, full_name, admission_number, role FROM tbl_users WHERE admission_number = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, admissionNumber);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getInt("admission_number"),
                            rs.getString("role")
                    );
                    User.setCurrentUser(user);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<User> getCandidates() throws SQLException {
        List<User> candidates = new ArrayList<>();
        String sql = "SELECT id, full_name, admission_number, role FROM tbl_users WHERE role = 'candidate'";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                candidates.add(new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getInt("admission_number"),
                        rs.getString("role")
                ));
            }
        }
        return candidates;
    }
}