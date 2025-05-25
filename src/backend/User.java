package backend;

public class User {
    private final int id;
    private final String fullName;
    private final int admissionNumber;
    private final String role;

    public User(int id, String fullName, int admissionNumber, String role) {
        this.id = id;
        this.fullName = fullName;
        this.admissionNumber = admissionNumber;
        this.role = role.toLowerCase();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return fullName; }
    public int getAdmissionNumber() { return admissionNumber; }
    public String getRole() { return role; }

    // Role checks
    public boolean isStudent() { return "student".equals(role); }
    public boolean isCandidate() { return "candidate".equals(role); }
    public boolean isAdmin() { return "admin".equals(role); }
}