package guichafy.sample_api.domain.valueobjects;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!isValidEmail(value)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        value = value.toLowerCase();
    }

    private static boolean isValidEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        if (email.contains("..")) {
            return false;
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }
        String localPart = parts[0];
        String domainPart = parts[1];
        
        if (localPart.startsWith(".") || localPart.endsWith(".") ||
            domainPart.startsWith(".") || domainPart.endsWith(".")) {
            return false;
        }
        
        return true;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
