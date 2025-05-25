package backend;

import java.sql.*;

public class AdminDAO {
    public static boolean changeUserRole(String admissionNumber, String newRole) {
        String sql = "UPDATE tbl_users SET role = ? WHERE admission_number = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newRole);
            stmt.setString(2, admissionNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}