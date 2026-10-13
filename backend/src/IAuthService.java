import java.util.List;

/**
 * IAuthService interface defining the service layer contract for authentication.
 * Demonstrates:
 * - Service Layer Pattern
 * - Abstraction
 * - Dependency Inversion Principle (DIP)
 * - Compile-Time Polymorphism (Method Overloading on login)
 */
public interface IAuthService {

    /**
     * Authenticates a user with explicit username and plain-text password.
     *
     * @param username Provided username
     * @param password Provided plain-text password
     * @return The authenticated polymorphic User instance
     * @throws AuthenticationException When credentials are invalid or system fails
     */
    User login(String username, String password) throws AuthenticationException;

    /**
     * Overloaded login method accepting an encapsulated LoginCredentials value object.
     * Demonstrates Static (Compile-Time) Polymorphism.
     *
     * @param credentials Value object containing credentials
     * @return The authenticated polymorphic User instance
     * @throws AuthenticationException When credentials are invalid or system fails
     */
    User login(LoginCredentials credentials) throws AuthenticationException;

    /**
     * Terminates the current active session.
     */
    void logout();

    /**
     * Retrieves the currently authenticated User entity.
     *
     * @return Current User or null if unauthenticated
     */
    User getCurrentUser();

    /**
     * Checks if a user is currently authenticated in this session.
     */
    boolean isAuthenticated();

    /**
     * Polymorphically verifies if the current session user holds the required permission.
     */
    boolean checkPermission(String permission);

    /**
     * Registers a new user into the database.
     */
    int registerUser(User user, String plainPassword) throws AuthenticationException;

    /**
     * Retrieves all system users (subject to admin access verification).
     */
    List<User> listAllUsers() throws AuthenticationException;
}
