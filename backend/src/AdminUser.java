import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Concrete AdminUser entity representing system administrators.
 * Demonstrates:
 * - Inheritance: Extends User (and transitively Person)
 * - Polymorphism: Overrides getPermissions(), hasPermission(), getRoleDescription()
 * - Role-Based Access Control (RBAC): Universal access authority
 */
public class AdminUser extends User {

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
