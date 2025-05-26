package backend;

public class Vote {
    private int id;
    private String voterAdmissionNumber;
    private String candidateAdmissionNumber;

    // Constructors
    public Vote() {}

    public Vote(int id, String voterAdmissionNumber, String candidateAdmissionNumber) {
        this.id = id;
        this.voterAdmissionNumber = voterAdmissionNumber;
        this.candidateAdmissionNumber = candidateAdmissionNumber;
    }

    public Vote(String voterAdmissionNumber, String candidateAdmissionNumber) {
        this.voterAdmissionNumber = voterAdmissionNumber;
        this.candidateAdmissionNumber = candidateAdmissionNumber;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoterAdmissionNumber() {
        return voterAdmissionNumber;
    }

    public void setVoterAdmissionNumber(String voterAdmissionNumber) {
        this.voterAdmissionNumber = voterAdmissionNumber;
    }

    public String getCandidateAdmissionNumber() {
        return candidateAdmissionNumber;
    }

    public void setCandidateAdmissionNumber(String candidateAdmissionNumber) {
        this.candidateAdmissionNumber = candidateAdmissionNumber;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", voterAdmissionNumber='" + voterAdmissionNumber + '\'' +
                ", candidateAdmissionNumber='" + candidateAdmissionNumber + '\'' +
                '}';
    }
}
