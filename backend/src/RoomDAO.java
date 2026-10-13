import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RoomDAO implements IRoomDAO.
 * Handles persistence and retrieval of Room domain entities with MySQL.
 * Demonstrates DAO Pattern, Factory Pattern integration, and Polymorphic Entity Mapping.
 */
public class RoomDAO implements IRoomDAO {

    // Display all rooms from MySQL (used by Swing console/UI)
    @Override
    public void displayRooms() throws SQLException {
        List<Room> rooms = getAllRoomEntities();
        System.out.println("----------------------------------------");
        System.out.println("            HOTEL ROOMS LIST            ");
        System.out.println("----------------------------------------");
        for (Room r : rooms) {
            r.displayDetails();
        }
    }

    // Retrieve all rooms as polymorphic Room entities using RoomFactory
    @Override
    public List<Room> getAllRoomEntities() throws SQLException {
        String sql = "SELECT room_id, room_number, room_type, price_per_night, status FROM rooms ORDER BY room_id";
        List<Room> rooms = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("room_id");
                String num = rs.getString("room_number");
                String type = rs.getString("room_type");
                double price = rs.getBigDecimal("price_per_night").doubleValue();
                RoomStatus status = RoomStatus.fromString(rs.getString("status"));

                // Polymorphic instantiation via Factory Pattern
                Room room = RoomFactory.createRoom(id, num, type, price, status);
                rooms.add(room);
            }
        }
        return rooms;
    }

    // Retrieve single room by ID as polymorphic Room entity
    @Override
    public Room getRoomEntityById(int roomId) throws SQLException {
        String sql = "SELECT room_id, room_number, room_type, price_per_night, status FROM rooms WHERE room_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("room_id");
                    String num = rs.getString("room_number");
                    String type = rs.getString("room_type");
                    double price = rs.getBigDecimal("price_per_night").doubleValue();
                    RoomStatus status = RoomStatus.fromString(rs.getString("status"));

                    return RoomFactory.createRoom(id, num, type, price, status);
                }
            }
        }
        return null;
    }

    // Save room using typed Room domain entity
    @Override
    public void addRoom(Room room) throws SQLException {
        if (room == null) throw new IllegalArgumentException("Room cannot be null.");
        addRoom(room.getRoomNumber(), room.getRoomType(), room.getPricePerNight());
    }

    // Add a room by parameters
    @Override
    public void addRoom(String roomNumber, String roomType, double pricePerNight) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, status) VALUES (?, ?, ?, 'Available')";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setString(2, roomType);
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(pricePerNight));
            ps.executeUpdate();
            System.out.println("Room #" + roomNumber + " added successfully.");
        }
    }

    // Get all rooms as structured list of maps (preserves full Web API compatibility)
    public List<Map<String, Object>> getAllRooms() throws SQLException {
        List<Room> rooms = getAllRoomEntities();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Room r : rooms) {
            list.add(r.toMap());
        }
        return list;
    }

    // Get single room by ID as map (preserves Web API compatibility)
    public Map<String, Object> getRoomById(int roomId) throws SQLException {
        Room room = getRoomEntityById(roomId);
        return room != null ? room.toMap() : null;
    }
}