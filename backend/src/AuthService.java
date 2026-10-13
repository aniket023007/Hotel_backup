import java.sql.SQLException;
import java.util.List;

/**
 * AuthService implements IAuthService.
 * Orchestrates business logic for user authentication, password verification,
 * session maintenance, and authorization checks.
 * Demonstrates:
 * - Service Layer Pattern
 * - Dependency Injection (Accepts IUserDAO abstraction)
 * - Singleton Pattern (Provides synchronized global session access)
 * - Encapsulation (Guarded session state)
 * - Compile-Time Polymorphism (Method Overloading)
 */
public class AuthService implements IAuthService {

    private static AuthService instance;

    // Dependency Inversion: Depends on abstraction IUserDAO, not concrete MySQL class
    private final IUserDAO userDAO;

    // Encapsulation of session state
    private User currentUser;

    /**
     * Constructor with Dependency Injection.
     *
     * @param userDAO Injected data access implementation
     */
    public AuthService(IUserDAO userDAO) {
        if (userDAO == null) {
            throw new IllegalArgumentException("UserDAO cannot be null.");
        }
        this.userDAO = userDAO;
    }

    /**
     * Default constructor wiring standard UserDAO.
     */
    public AuthService() {
        this(new UserDAO());
    }

    /**
     * Singleton accessor for global session management across web and desktop layers.
     */
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    @Override
    public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password is required.");
        }

        try {
            User user = userDAO.findByUsername(username.trim());
            if (user == null) {
                System.out.println("[AUTH FAILED] User not found: " + username);
                throw new AuthenticationException("Invalid username or password.");
            }

            // Polymorphic & Encapsulated password verification
            if (!user.verifyPassword(password)) {
                System.out.println("[AUTH FAILED] Incorrect password for user: " + username);
                throw new AuthenticationException("Invalid username or password.");
            }

            this.currentUser = user;
            System.out.println("[AUTH SUCCESS] " + user.getName() + " logged in as " + user.getRole());
            return user;

        } catch (SQLException e) {
            System.err.println("[AUTH ERROR] Database error during login: " + e.getMessage());
            throw new AuthenticationException("Database error during authentication: " + e.getMessage(), e);
        }
    }

    /**
     * Overloaded login method accepting LoginCredentials value object.
     * Demonstrates Static (Compile-time) Polymorphism.
     */
    @Override
    public User login(LoginCredentials credentials) throws AuthenticationException {
        if (credentials == null) {
            throw new AuthenticationException("Credentials cannot be null.");
        }
        return login(credentials.getUsername(), credentials.getPassword());
    }

    @Override
    public void logout() {
        if (this.currentUser != null) {
            System.out.println("[AUTH LOGOUT] User logged out: " + currentUser.getUsername());
        }
        this.currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }

    @Override
    public boolean isAuthenticated() {
        return this.currentUser != null;
    }

    @Override
    public boolean checkPermission(String permission) {
        if (this.currentUser == null) return false;
        return this.currentUser.hasPermission(permission);
    }

    @Override
    public int registerUser(User user, String plainPassword) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User cannot be null.");
        }
        try {
            if (userDAO.userExists(user.getUsername())) {
                throw new AuthenticationException("Username '" + user.getUsername() + "' is already registered.");
            }
            return userDAO.saveUser(user, plainPassword);
        } catch (SQLException e) {
            throw new AuthenticationException("Failed to register user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> listAllUsers() throws AuthenticationException {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            throw new AuthenticationException("Failed to retrieve users: " + e.getMessage(), e);
        }
    }
}
