package backend;

import java.sql.*;

public class VoteDAO {

    // Check if user already voted
    public static boolean hasVoted(String admissionNumber) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT 1 FROM votes WHERE voter_admission = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, admissionNumber);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        }
    }

    // Cast vote for a candidate
    public static void castVote(String voterAdmission, String candidateAdmission) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            // Check if already voted
            if (hasVoted(voterAdmission)) {
                throw new SQLException("User has already voted");
            }
            // Insert vote
            String sql = "INSERT INTO votes (voter_admission, candidate_admission) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, voterAdmission);
                stmt.setString(2, candidateAdmission);
                stmt.executeUpdate();
            }
        }
    }
}
