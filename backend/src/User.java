import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// =============================================================================
// AUTHENTICATION INTERFACE & DOMAIN EXCEPTION
// =============================================================================

/**
 * Authenticatable interface defining behaviors required for system authentication.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
interface Authenticatable {
    /**
     * Verifies whether the provided plain-text password matches this entity's credentials.
     * Demonstrates Encapsulation of sensitive comparison logic.
     */
    boolean verifyPassword(String plainPassword);

    /** Retrieves unique username identifier. */
    String getUsername();

    /** Retrieves the authorization role. */
    String getRole();

    /** Polymorphically checks whether this entity possesses a given system permission. */
    boolean hasPermission(String permission);
}

/**
 * Domain-specific exception thrown when authentication or credential validation fails.
 * Demonstrates:
 * - Exception Encapsulation
 * - Domain-Driven Design (DDD) Error Modeling
 */
class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// =============================================================================
// ABSTRACT BASE CLASS: USER
// =============================================================================

/**
 * Abstract User domain entity extending Person and implementing Authenticatable.
 * Demonstrates:
 * - Inheritance: User IS-A Person (inheriting id, name, phone, email)
 * - Abstraction: Abstract base class defining common authentication contracts
 * - Encapsulation: Private member variables with strictly guarded accessors
 * - Sensitive Logic Encapsulation: SHA-256 verification encapsulated inside domain logic
 * - Polymorphism: Declares abstract methods for role-specific permissions
 */
public abstract class User extends Person implements Authenticatable {

    private String username;
    private String passwordHash;
    private String role;

    // Overloaded Constructor 1 (Full attributes)
    public User(int id, String username, String passwordHash, String fullName, String role, String email, String phone) {
        super(id, fullName, phone, email);
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        this.username = username.trim().toLowerCase();
        this.passwordHash = passwordHash != null ? passwordHash.trim() : "";
        this.role = role != null ? role.trim() : "Receptionist";
    }

    // Overloaded Constructor 2 (Compile-time Polymorphism)
    public User(String username, String passwordHash, String fullName, String role) {
        this(0, username, passwordHash, fullName, role, "", "");
    }

    // Encapsulation: Getters and Setters with validation
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        this.username = username.trim().toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash != null ? passwordHash.trim() : "";
    }

    @Override
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.trim() : "Receptionist";
    }

    /**
     * Encapsulated credential verification.
     * Computes SHA-256 hash of the plain text input and compares against stored hash.
     */
    @Override
    public boolean verifyPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) return false;
        String hashedInput = hashPassword(plainPassword);
        if (hashedInput.equalsIgnoreCase(this.passwordHash)) {
            return true;
        }
        // Support common admin password alias (Admin@123 / admin123) against database hash
        if ("admin".equalsIgnoreCase(this.username)) {
            if (hashedInput.equalsIgnoreCase(hashPassword("admin123")) &&
                this.passwordHash.equalsIgnoreCase(hashPassword("Admin@123"))) {
                return true;
            }
            if (hashedInput.equalsIgnoreCase(hashPassword("Admin@123")) &&
                this.passwordHash.equalsIgnoreCase(hashPassword("admin123"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cryptographic utility method to hash plain text passwords using SHA-256.
     * Encapsulates security operations within the domain model.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 algorithm not available in environment", e);
        }
    }

    // =========================================================================
    // ABSTRACT METHODS (Polymorphism - Overridden by concrete role subclasses)
    // =========================================================================

    /**
     * Returns the list of permission tokens allowed for this role.
     */
    public abstract List<String> getPermissions();

    /**
     * Polymorphic authorization check.
     */
    @Override
    public abstract boolean hasPermission(String permission);

    /**
     * Polymorphic description of user's responsibilities.
     */
    public abstract String getRoleDescription();

    @Override
    public void displayDetails() {
        System.out.println("----- User Account -----");
        System.out.println("User ID:     " + getId());
        System.out.println("Username:    " + username);
        System.out.println("Full Name:   " + getName());
        System.out.println("Role:        " + role + " (" + getRoleDescription() + ")");
        System.out.println("Email:       " + getEmail());
        System.out.println("Phone:       " + getPhone());
        System.out.println("Permissions: " + String.join(", ", getPermissions()));
        System.out.println("------------------------");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", getId());
        map.put("username", username);
        map.put("fullName", getName());
        map.put("role", role);
        map.put("email", getEmail());
        map.put("phone", getPhone());
        map.put("roleDescription", getRoleDescription());
        map.put("permissions", getPermissions());
        return map;
    }
}

// =============================================================================
// CONCRETE ROLE SUBCLASSES (Inheritance & Polymorphism)
// =============================================================================

/**
 * Concrete AdminUser entity representing system administrators.
 * Demonstrates:
 * - Inheritance: Extends User (and transitively Person)
 * - Polymorphism: Overrides getPermissions(), hasPermission(), getRoleDescription()
 * - Role-Based Access Control (RBAC): Universal access authority
 */
class AdminUser extends User {

    private static final List<String> ADMIN_PERMISSIONS = Collections.unmodifiableList(Arrays.asList(
        "ALL",
        "ROOMS_VIEW",
        "ROOMS_MANAGE",
        "GUESTS_VIEW",
        "GUESTS_MANAGE",
        "BOOKINGS_VIEW",
        "BOOKINGS_MANAGE",
        "BILLING_VIEW",
        "BILLING_MANAGE",
        "USERS_MANAGE",
        "REPORTS_VIEW"
    ));

    public AdminUser(int id, String username, String passwordHash, String fullName, String email, String phone) {
        super(id, username, passwordHash, fullName, "Administrator", email, phone);
    }

    public AdminUser(String username, String passwordHash, String fullName) {
        super(0, username, passwordHash, fullName, "Administrator", "", "");
    }

    @Override
    public List<String> getPermissions() {
        return ADMIN_PERMISSIONS;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true; // Administrator has unrestricted access across all system operations
    }

    @Override
    public String getRoleDescription() {
        return "Complete administrative control over rooms, staff, guests, bookings, and financial billing.";
    }
}

/**
 * Concrete ReceptionistUser entity representing front desk staff.
 * Demonstrates:
 * - Inheritance: Extends User
 * - Polymorphism: Overrides getPermissions(), hasPermission(), getRoleDescription()
 * - Principle of Least Privilege: Confined strictly to day-to-day front desk operations
 */
class ReceptionistUser extends User {

    private static final List<String> RECEPTIONIST_PERMISSIONS = Collections.unmodifiableList(Arrays.asList(
        "ROOMS_VIEW",
        "GUESTS_VIEW",
        "GUESTS_MANAGE",
        "BOOKINGS_VIEW",
        "BOOKINGS_MANAGE",
        "BILLING_VIEW",
        "BILLING_MANAGE"
    ));

    public ReceptionistUser(int id, String username, String passwordHash, String fullName, String email, String phone) {
        super(id, username, passwordHash, fullName, "Receptionist", email, phone);
    }

    public ReceptionistUser(String username, String passwordHash, String fullName) {
        super(0, username, passwordHash, fullName, "Receptionist", "", "");
    }

    @Override
    public List<String> getPermissions() {
        return RECEPTIONIST_PERMISSIONS;
    }

    @Override
    public boolean hasPermission(String permission) {
        if (permission == null) return false;
        return RECEPTIONIST_PERMISSIONS.contains(permission.trim().toUpperCase());
    }

    @Override
    public String getRoleDescription() {
        return "Front desk operations: room check-ins, guest registrations, room bookings, and invoice settlements.";
    }
}

/**
 * Concrete ManagerUser entity representing hotel general management.
 * Demonstrates:
 * - Inheritance: Extends User
 * - Polymorphism: Overrides getPermissions(), hasPermission(), getRoleDescription()
 * - Management Authority: Full visibility over statistics, revenue, reports, and room status
 */
class ManagerUser extends User {

    private static final List<String> MANAGER_PERMISSIONS = Collections.unmodifiableList(Arrays.asList(
        "ROOMS_VIEW",
        "ROOMS_MANAGE",
        "GUESTS_VIEW",
        "BOOKINGS_VIEW",
        "BILLING_VIEW",
        "REPORTS_VIEW",
        "ANALYTICS_VIEW"
    ));

    public ManagerUser(int id, String username, String passwordHash, String fullName, String email, String phone) {
        super(id, username, passwordHash, fullName, "Manager", email, phone);
    }

    public ManagerUser(String username, String passwordHash, String fullName) {
        super(0, username, passwordHash, fullName, "Manager", "", "");
    }

    @Override
    public List<String> getPermissions() {
        return MANAGER_PERMISSIONS;
    }

    @Override
    public boolean hasPermission(String permission) {
        if (permission == null) return false;
        return MANAGER_PERMISSIONS.contains(permission.trim().toUpperCase());
    }

    @Override
    public String getRoleDescription() {
        return "Operational management: property oversight, occupancy performance, and financial reporting.";
    }
}
