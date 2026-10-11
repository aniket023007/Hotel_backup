import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GuestDAO implements IGuestDAO.
 * Handles persistence and retrieval of Guest domain entities with MySQL.
 * Demonstrates DAO Pattern, Data Abstraction, and Object Mapping.
 */
public class GuestDAO implements IGuestDAO {

    // Save a new guest (used by Swing UI)
    @Override
    public void addGuest(
            String fullName,
            String phone,
            String email,
            String address) throws SQLException {

        addGuestAndGetId(fullName, phone, email, address);
    }

    // Save a new guest and return auto-generated guest_id
    @Override
    public int addGuestAndGetId(
            String fullName,
            String phone,
            String email,
            String address) throws SQLException {

        String sql = "INSERT INTO guests (full_name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, fullName != null ? fullName.trim() : "");
            ps.setString(2, phone != null ? phone.trim() : "");
            ps.setString(3, email != null ? email.trim() : "");
            ps.setString(4, address != null ? address.trim() : "");

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("Guest saved to MySQL successfully with ID #" + generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    // Strongly-typed OOP method accepting domain entity
    @Override
    public int saveGuest(Guest guest) throws SQLException {
        if (guest == null) throw new IllegalArgumentException("Guest entity cannot be null.");
        int id = addGuestAndGetId(guest.getName(), guest.getPhone(), guest.getEmail(), guest.getAddress());
        guest.setId(id);
        return id;
    }

    // Display all guests (used by Swing UI console)
    @Override
    public void displayGuests() throws SQLException {
        List<Guest> guests = getAllGuestEntities();
        System.out.println("----------------------------------------");
        System.out.println("          REGISTERED GUESTS             ");
        System.out.println("----------------------------------------");
        for (Guest g : guests) {
            g.displayDetails();
        }
    }

    // Retrieve all guests as strongly-typed domain entities
    @Override
    public List<Guest> getAllGuestEntities() throws SQLException {
        String sql = "SELECT guest_id, full_name, phone, email, address FROM guests ORDER BY guest_id DESC";
        List<Guest> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("guest_id"),
                    rs.getString("full_name"),
                    rs.getString("phone") != null ? rs.getString("phone") : "",
                    rs.getString("email") != null ? rs.getString("email") : "",
                    rs.getString("address") != null ? rs.getString("address") : ""
                );
                list.add(guest);
            }
        }
        return list;
    }

    // Retrieve single guest as strongly-typed domain entity
    @Override
    public Guest getGuestEntityById(int guestId) throws SQLException {
        String sql = "SELECT guest_id, full_name, phone, email, address FROM guests WHERE guest_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("full_name"),
                        rs.getString("phone") != null ? rs.getString("phone") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getString("address") != null ? rs.getString("address") : ""
                    );
                }
            }
        }
        return null;
    }

    // Get all guests as list of maps (preserves full Web API compatibility)
    public List<Map<String, Object>> getAllGuests() throws SQLException {
        List<Guest> guests = getAllGuestEntities();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Guest g : guests) {
            maps.add(g.toMap());
        }
        return maps;
    }

    // Get guest by ID as map (preserves Web API compatibility)
    public Map<String, Object> getGuestById(int guestId) throws SQLException {
        Guest guest = getGuestEntityById(guestId);
        return guest != null ? guest.toMap() : null;
    }
}