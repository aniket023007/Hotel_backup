import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// =============================================================================
// USER DATA ACCESS INTERFACE
// =============================================================================

/**
 * IUserDAO interface defining persistence contracts for user entities.
 * Demonstrates:
 * - Abstraction: Hides low-level MySQL queries from business layers
 * - DAO Pattern: Decouples domain logic from persistence technology
 * - Dependency Inversion Principle: High-level modules depend on this abstraction
 */
interface IUserDAO {

    /**
     * Finds a user by their unique username.
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
     */
    int saveUser(User user, String plainPassword) throws SQLException;

    /**
     * Checks if a username is already taken.
     */
    boolean userExists(String username) throws SQLException;
}

// =============================================================================
// USER DATA ACCESS OBJECT (DAO Pattern Implementation)
// =============================================================================

/**
 * UserDAO implements IUserDAO.
 * Handles database operations for the MySQL `users` table.
 * Demonstrates:
 * - Data Access Object (DAO) Pattern
 * - Data Abstraction: Encapsulates JDBC queries and connection lifecycle
 * - SQL Injection Protection: Strictly utilizes PreparedStatements
 * - Factory Integration: Maps relational tuples into polymorphic User domain objects
 */
public class UserDAO implements IUserDAO {

    @Override
    public User findByUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT user_id, username, password_hash, full_name, role, email, phone " +
                     "FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User findById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, full_name, role, email, phone " +
                     "FROM users WHERE user_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash, full_name, role, email, phone " +
                     "FROM users ORDER BY user_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        return users;
    }

    @Override
    public int saveUser(User user, String plainPassword) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User entity cannot be null.");
        }

        String sql = "INSERT INTO users (username, password_hash, full_name, role, email, phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        String hash = (plainPassword != null && !plainPassword.isEmpty())
                ? User.hashPassword(plainPassword)
                : user.getPasswordHash();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, hash);
            ps.setString(3, user.getName());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());

            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    int generatedId = gk.getInt(1);
                    user.setId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean userExists(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) return false;
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Maps an active ResultSet row into a polymorphic User instance using the UserFactory.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");
        String email = rs.getString("email");
        String phone = rs.getString("phone");

        return UserFactory.createUser(id, username, passwordHash, fullName, role, email, phone);
    }
}
