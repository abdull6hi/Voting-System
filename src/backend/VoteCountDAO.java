package backend;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VoteCountDAO {
    private static final String GET_VOTE_COUNTS_SQL =
            "SELECT u.admission_number, u.full_name, COUNT(v.id) AS vote_count " +
                    "FROM tbl_users u LEFT JOIN tbl_votes v ON u.admission_number = v.candidate_admission " +
                    "WHERE u.role = 'candidate' " +
                    "GROUP BY u.admission_number, u.full_name " +
                    "ORDER BY vote_count DESC";

    private static final String GET_TOTAL_VOTES_SQL =
            "SELECT COUNT(*) AS total FROM tbl_votes";

    public static Map<String, CandidateResult> getVoteResults() throws SQLException {
        Map<String, CandidateResult> results = new LinkedHashMap<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_VOTE_COUNTS_SQL)) {

            int totalVotes = getTotalVotes(conn);

            while (rs.next()) {
                String admission = rs.getString("admission_number");
                String name = rs.getString("full_name");
                int votes = rs.getInt("vote_count");
                double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0;

                results.put(admission, new CandidateResult(name, votes, percentage));
            }
        }
        return results;
    }

    private static int getTotalVotes(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_TOTAL_VOTES_SQL)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    public static class CandidateResult {
        private final String name;
        private final int votes;
        private final double percentage;

        public CandidateResult(String name, int votes, double percentage) {
            this.name = name;
            this.votes = votes;
            this.percentage = percentage;
        }

        public String getName() { return name; }
        public int getVotes() { return votes; }
        public double getPercentage() { return percentage; }
    }
}