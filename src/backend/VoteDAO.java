package backend;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class VoteDAO {
    public static boolean castVote(String voterAdmission, String candidateAdmission) {
        String sql = "INSERT INTO tbl_votes (voter_admission, candidate_admission) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, voterAdmission);
            stmt.setString(2, candidateAdmission);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasVoted(String admissionNumber) {
        String sql = "SELECT 1 FROM tbl_votes WHERE voter_admission = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, admissionNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, Integer> getVoteCounts() {
        Map<String, Integer> voteCounts = new HashMap<>();
        String sql = "SELECT candidate_admission, COUNT(*) as vote_count FROM tbl_votes GROUP BY candidate_admission";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                voteCounts.put(rs.getString("candidate_admission"), rs.getInt("vote_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voteCounts;
    }
}