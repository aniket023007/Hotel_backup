import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Concrete ManagerUser entity representing hotel general management.
 * Demonstrates:
 * - Inheritance: Extends User
 * - Polymorphism: Overrides getPermissions(), hasPermission(), getRoleDescription()
 * - Management Authority: Full visibility over statistics, revenue, reports, and room status
 */
public class ManagerUser extends User {

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
