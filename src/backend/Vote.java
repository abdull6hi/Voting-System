package backend;

import java.time.LocalDateTime;

public class Vote {
    private int id;
    private String voterAdmission;
    private String candidateAdmission;
    private LocalDateTime voteTime;

    // Constructor for creating new votes (before DB insertion)
    public Vote(String voterAdmission, String candidateAdmission) {
        this.voterAdmission = voterAdmission;
        this.candidateAdmission = candidateAdmission;
        this.voteTime = LocalDateTime.now();
    }

    // Constructor for existing votes (from DB)
    public Vote(int id, String voterAdmission, String candidateAdmission, LocalDateTime voteTime) {
        this.id = id;
        this.voterAdmission = voterAdmission;
        this.candidateAdmission = candidateAdmission;
        this.voteTime = voteTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getVoterAdmission() {
        return voterAdmission;
    }

    public String getCandidateAdmission() {
        return candidateAdmission;
    }

    public LocalDateTime getVoteTime() {
        return voteTime;
    }

    // Setters (only for fields that might need modification)
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(
                "Vote [id=%d, voter=%s, candidate=%s, time=%s]",
                id, voterAdmission, candidateAdmission, voteTime
        );
    }
}