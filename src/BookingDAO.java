import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 * BookingDAO implements IBookingDAO.
 * Handles persistence and retrieval of Booking domain entities with MySQL.
 * Demonstrates DAO Pattern, Transaction Management, and Object Composition (Booking HAS-A Guest and Room).
 */
public class BookingDAO implements IBookingDAO {

    @Override
    public void bookRoom(
            int bookingId,
            int guestId,
            int roomId,
            int numberOfNights) throws SQLException {

        if (bookingId <= 0 || guestId <= 0 || roomId <= 0 || numberOfNights <= 0) {
            throw new IllegalArgumentException("All IDs and nights must be positive integers.");
        }

        String bookingSQL =
            "INSERT INTO bookings (booking_id, guest_id, room_id, number_of_nights, status) " +
            "VALUES (?, ?, ?, ?, 'Booked')";

        String roomSQL =
            "UPDATE rooms SET status = 'Booked' " +
            "WHERE room_id = ? AND status = 'Available'";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try (
                PreparedStatement roomStatement = con.prepareStatement(roomSQL);
                PreparedStatement bookingStatement = con.prepareStatement(bookingSQL)
            ) {
                roomStatement.setInt(1, roomId);
                int updated = roomStatement.executeUpdate();

                if (updated == 0) {
                    throw new SQLException("Room #" + roomId + " is not available or does not exist.");
                }

                bookingStatement.setInt(1, bookingId);
                bookingStatement.setInt(2, guestId);
                bookingStatement.setInt(3, roomId);
                bookingStatement.setInt(4, numberOfNights);

                bookingStatement.executeUpdate();
                con.commit();

                System.out.println("Room booked successfully! Reservation ID: #" + bookingId);

            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Strongly-typed booking method accepting domain entity
    @Override
    public void bookRoom(Booking booking) throws SQLException {
        if (booking == null) throw new IllegalArgumentException("Booking cannot be null.");
        bookRoom(
            booking.getBookingId(),
            booking.getGuest().getId(),
            booking.getRoom().getRoomId(),
            booking.getNumberOfNights()
        );
    }

    // Retrieve bookings as strongly-typed domain entities composed of Guest and Room
    @Override
    public List<Booking> getAllBookingEntities(Boolean activeOnly) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT b.booking_id, b.guest_id, b.room_id, b.number_of_nights, b.booking_date, b.status, " +
            "g.full_name, g.phone, g.email, g.address, " +
            "r.room_number, r.room_type, r.price_per_night, r.status AS room_status " +
            "FROM bookings b " +
            "JOIN guests g ON b.guest_id = g.guest_id " +
            "JOIN rooms r ON b.room_id = r.room_id "
        );

        if (activeOnly != null && activeOnly) {
            sql.append("WHERE b.status = 'Booked' ");
        }
        sql.append("ORDER BY b.booking_id DESC");

        List<Booking> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("guest_id"),
                    rs.getString("full_name"),
                    rs.getString("phone") != null ? rs.getString("phone") : "",
                    rs.getString("email") != null ? rs.getString("email") : "",
                    rs.getString("address") != null ? rs.getString("address") : ""
                );

                Room room = RoomFactory.createRoom(
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getBigDecimal("price_per_night").doubleValue(),
                    RoomStatus.fromString(rs.getString("room_status"))
                );

                Booking booking = new Booking(
                    rs.getInt("booking_id"),
                    guest,
                    room,
                    rs.getInt("number_of_nights"),
                    rs.getTimestamp("booking_date") != null ? rs.getTimestamp("booking_date").toString() : "",
                    BookingStatus.fromString(rs.getString("status"))
                );

                list.add(booking);
            }
        }
        return list;
    }

    // Retrieve single booking entity by ID
    @Override
    public Booking getBookingEntityById(int bookingId) throws SQLException {
        String sql =
            "SELECT b.booking_id, b.guest_id, b.room_id, b.number_of_nights, b.booking_date, b.status, " +
            "g.full_name, g.phone, g.email, g.address, " +
            "r.room_number, r.room_type, r.price_per_night, r.status AS room_status " +
            "FROM bookings b " +
            "JOIN guests g ON b.guest_id = g.guest_id " +
            "JOIN rooms r ON b.room_id = r.room_id " +
            "WHERE b.booking_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("full_name"),
                        rs.getString("phone") != null ? rs.getString("phone") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getString("address") != null ? rs.getString("address") : ""
                    );

                    Room room = RoomFactory.createRoom(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getBigDecimal("price_per_night").doubleValue(),
                        RoomStatus.fromString(rs.getString("room_status"))
                    );

                    return new Booking(
                        rs.getInt("booking_id"),
                        guest,
                        room,
                        rs.getInt("number_of_nights"),
                        rs.getTimestamp("booking_date") != null ? rs.getTimestamp("booking_date").toString() : "",
                        BookingStatus.fromString(rs.getString("status"))
                    );
                }
            }
        }
        return null;
    }

    // Fetch bookings into a table model (used by Swing UI)
    @Override
    public DefaultTableModel getBookingsTableModel() throws SQLException {
        List<Booking> bookings = getAllBookingEntities(false);

        String[] columns = {
            "Booking ID",
            "Guest Name",
            "Phone",
            "Room No.",
            "Room Type",
            "Nights",
            "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Booking b : bookings) {
            Object[] row = {
                b.getBookingId(),
                b.getGuest().getName(),
                b.getGuest().getPhone(),
                b.getRoom().getRoomNumber(),
                b.getRoom().getRoomType(),
                b.getNumberOfNights(),
                b.getStatus().getDisplayName()
            };
            model.addRow(row);
        }

        return model;
    }

    // Get next available booking ID
    @Override
    public int getNextBookingId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(booking_id), 0) + 1 AS next_id FROM bookings";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        }
        return 1;
    }

    // Get bookings as list of maps for Web API (preserves full compatibility)
    public List<Map<String, Object>> getAllBookings(Boolean activeOnly) throws SQLException {
        List<Booking> bookings = getAllBookingEntities(activeOnly);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Booking b : bookings) {
            list.add(b.toMap());
        }
        return list;
    }

    // Get specific booking details as map
    public Map<String, Object> getBookingById(int bookingId) throws SQLException {
        Booking b = getBookingEntityById(bookingId);
        return b != null ? b.toMap() : null;
    }
}
