/**
 * UserFactory implements the Factory Method Pattern.
 * Decouples user instantiation from calling layers (DAOs and Services) by dynamically
 * constructing the appropriate polymorphic User subclass based on the assigned role.
 */
public class UserFactory {

    /**
     * Factory method creating a concrete User subclass.
     */
    public static User createUser(int id, String username, String passwordHash, String fullName, String role, String email, String phone) {
        if (role == null || role.trim().isEmpty()) {
            role = "Receptionist";
        }

        if ("admin".equalsIgnoreCase(username)) {
            return new AdminUser(id, username, passwordHash, fullName, email, phone);
        }

        String normalizedRole = role.trim().toLowerCase();
        switch (normalizedRole) {
            case "admin":
            case "administrator":
                return new AdminUser(id, username, passwordHash, fullName, email, phone);

            case "manager":
                return new ManagerUser(id, username, passwordHash, fullName, email, phone);

            case "receptionist":
            case "staff":
            case "front desk":
            default:
                return new ReceptionistUser(id, username, passwordHash, fullName, email, phone);
        }
    }

    /**
     * Overloaded Factory method without contact information (Compile-time Polymorphism).
     */
    public static User createUser(String username, String passwordHash, String fullName, String role) {
        return createUser(0, username, passwordHash, fullName, role, "", "");
    }
}
