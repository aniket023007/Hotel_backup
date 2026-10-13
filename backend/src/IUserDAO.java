import java.sql.SQLException;
import java.util.List;

/**
 * IUserDAO interface defining persistence contracts for user entities.
 * Demonstrates:
 * - Abstraction: Hides low-level MySQL queries from business layers
 * - DAO Pattern: Decouples domain logic from persistence technology
 * - Dependency Inversion Principle: High-level modules depend on this abstraction
 */
public interface IUserDAO {

    /**
     * Finds a user by their unique username.
     *
     * @param username Case-insensitive username
     * @return Concrete polymorphic User object, or null if not found
     * @throws SQLException On database failure
     */
    User findByUsername(String username) throws SQLException;

    /**
     * Finds a user by their primary key user_id.
     */
    User findById(int userId) throws SQLException;

    /**
     * Retrieves all registered system users.
     */
    List<User> getAllUsers() throws SQLException;

    /**
     * Persists a new user into MySQL with an encrypted SHA-256 password.
     *
     * @param user          Polymorphic User entity
     * @param plainPassword Plain-text password to hash and store
     * @return Generated primary key ID
     * @throws SQLException On database failure
     */
    int saveUser(User user, String plainPassword) throws SQLException;

    /**
     * Checks if a username is already taken.
     */
    boolean userExists(String username) throws SQLException;
}
