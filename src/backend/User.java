package backend;

public class User {
    private String admissionNumber;
    private String name;
    private String role; // "admin", "candidate", "student", etc.

    private static User currentUser; // For tracking logged-in user globally (if you use this pattern)

    public User(String admissionNumber, String name, String role) {
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.role = role;
    }

    // Getters
    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    // Role checks
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isCandidate() {
        return "candidate".equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return "student".equalsIgnoreCase(role);
    }

    // Static currentUser management (if you use this to store logged-in user)
    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
