/**
 * Authenticatable interface defining behaviors required for system authentication.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
public interface Authenticatable {

    /**
     * Verifies whether the provided plain-text password matches this entity's credentials.
     * Demonstrates Encapsulation of sensitive comparison logic.
     *
     * @param plainPassword The plain text password entered by the user
     * @return true if valid, false otherwise
     */
    boolean verifyPassword(String plainPassword);

    /**
     * Retrieves unique username identifier.
     */
    String getUsername();

    /**
     * Retrieves the authorization role.
     */
    String getRole();

    /**
     * Polymorphically checks whether this entity possesses a given system permission.
     */
    boolean hasPermission(String permission);
}
