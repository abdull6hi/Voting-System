package com.voting.util;

import java.util.regex.Pattern;

/**
 * Validates and sanitizes all user inputs to prevent injection attacks.
 */
public final class InputValidator {
    // Regex patterns (compiled once for performance)
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]{2,50}$");
    private static final Pattern BIO_PATTERN = Pattern.compile("^[\\p{L}0-9 .,!?@#$%&*()\\-]{0,200}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");

    private InputValidator() {}

    //── Validation Methods ──────────────────────────────────────────────//
    public static boolean isValidId(String id) {
        return id != null && ID_PATTERN.matcher(id).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidBio(String bio) {
        return bio == null || BIO_PATTERN.matcher(bio).matches();
    }

    public static boolean isStrongPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    //── Sanitization Methods ───────────────────────────────────────────//
    public static String sanitizeText(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", ""); // Strip HTML tags
    }

    public static String sanitizeSql(String input) {
        if (input == null) return null;
        return input.replace("'", "''")
                .replace("\\", "\\\\");
    }

    //── Composite Validation ───────────────────────────────────────────//
    public static void validateStudentInput(String id, String name, String password) {
        if (!isValidId(id)) throw new IllegalArgumentException("Invalid student ID");
        if (!isValidName(name)) throw new IllegalArgumentException("Invalid name");
        if (!isStrongPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must be 8+ chars with uppercase, lowercase, digit, and special char"
            );
        }
    }
}