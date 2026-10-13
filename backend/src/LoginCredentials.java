/**
 * Value Object encapsulating login credentials.
 * Demonstrates:
 * - Encapsulation: Immutable private final fields
 * - Input Validation: Rejects invalid or null credentials early
 * - Data Transfer Object (DTO) Pattern
 */
public class LoginCredentials {

    private final String username;
    private final String password;

    public LoginCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        this.username = username.trim();
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginCredentials[username='" + username + "', password=******]";
    }
}
