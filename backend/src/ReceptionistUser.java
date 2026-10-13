import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Concrete ReceptionistUser entity representing front desk staff.
 * Demonstrates:
 * - Inheritance: Extends User
 * - Polymorphism: Overrides getPermissions(), hasPermission(), getRoleDescription()
 * - Principle of Least Privilege: Confined strictly to day-to-day front desk operations
 */
public class ReceptionistUser extends User {

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
