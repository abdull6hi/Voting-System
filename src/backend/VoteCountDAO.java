package backend;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class VoteCountDAO {

    /**
     * Returns a Map of candidate admission numbers to vote counts.
     * @return Map<String, Integer> candidateAdmission -> vote count
     * @throws SQLException
     */
    public static Map<String, Integer> getVoteCounts() throws SQLException {
        Map<String, Integer> voteCounts = new HashMap<>();

        String sql = "SELECT candidate_admission, COUNT(*) AS votes FROM votes GROUP BY candidate_admission";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String candidateAdmission = rs.getString("candidate_admission");
                int votes = rs.getInt("votes");
                voteCounts.put(candidateAdmission, votes);
            }
        }

        return voteCounts;
    }

    /**
     * Returns total number of votes cast.
     */
    public static int getTotalVotes() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM votes";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }
}
