import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

// =============================================================================
// BOOKING & CHECKOUT DATA ACCESS INTERFACES
// =============================================================================

/**
 * IBookingDAO interface defining the data access contract for Booking entities.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
interface IBookingDAO {
    void bookRoom(int bookingId, int guestId, int roomId, int numberOfNights) throws SQLException;
    void bookRoom(Booking booking) throws SQLException;
    List<Booking> getAllBookingEntities(Boolean activeOnly) throws SQLException;
    Booking getBookingEntityById(int bookingId) throws SQLException;
    int getNextBookingId() throws SQLException;
    DefaultTableModel getBookingsTableModel() throws SQLException;
}

/**
 * ICheckoutDAO interface defining the data access contract for Checkout & Billing.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
interface ICheckoutDAO {
    BigDecimal checkout(int bookingId) throws SQLException;
    BigDecimal calculateBill(int bookingId) throws SQLException;
    Bill getDetailedBillEntity(int bookingId) throws SQLException;
    Bill checkoutWithBillEntity(int bookingId) throws SQLException;
}

// =============================================================================
// BOOKING DATA ACCESS OBJECT (DAO Pattern Implementation)
// =============================================================================

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

// =============================================================================
// CHECKOUT DATA ACCESS OBJECT (Transaction & Checkout Management)
// =============================================================================

/**
 * CheckoutDAO implements ICheckoutDAO.
 * Handles checkout transactions and bill generation.
 * Demonstrates DAO Pattern, Transaction Management, and Strategy Pattern-driven Billing.
 */
class CheckoutDAO implements ICheckoutDAO {

    private final IBookingDAO bookingDAO;

    public CheckoutDAO() {
        this(new BookingDAO());
    }

    public CheckoutDAO(IBookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    @Override
    public BigDecimal checkout(int bookingId) throws SQLException {
        String selectSQL =
            "SELECT b.room_id, b.number_of_nights, r.price_per_night " +
            "FROM bookings b " +
            "JOIN rooms r ON b.room_id = r.room_id " +
            "WHERE b.booking_id = ? AND b.status = 'Booked'";

        String bookingSQL =
            "UPDATE bookings SET status = 'Checked Out' " +
            "WHERE booking_id = ? AND status = 'Booked'";

        String roomSQL =
            "UPDATE rooms SET status = 'Available' WHERE room_id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int roomId;
                int nights;
                BigDecimal price;

                try (PreparedStatement ps = con.prepareStatement(selectSQL)) {
                    ps.setInt(1, bookingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Active booking #" + bookingId + " not found or already checked out.");
                        }
                        roomId = rs.getInt("room_id");
                        nights = rs.getInt("number_of_nights");
                        price = rs.getBigDecimal("price_per_night");
                    }
                }

                BigDecimal total = price.multiply(BigDecimal.valueOf(nights));

                try (PreparedStatement ps = con.prepareStatement(bookingSQL)) {
                    ps.setInt(1, bookingId);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not update booking status to Checked Out.");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(roomSQL)) {
                    ps.setInt(1, roomId);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not update room status to Available.");
                    }
                }

                con.commit();
                return total;

            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public BigDecimal calculateBill(int bookingId) throws SQLException {
        String sql =
            "SELECT b.number_of_nights, r.price_per_night " +
            "FROM bookings b " +
            "JOIN rooms r ON b.room_id = r.room_id " +
            "WHERE b.booking_id = ? AND b.status = 'Booked'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Active booking not found for ID #" + bookingId);
                }
                BigDecimal price = rs.getBigDecimal("price_per_night");
                int nights = rs.getInt("number_of_nights");
                return price.multiply(BigDecimal.valueOf(nights));
            }
        }
    }

    // Strongly-typed Bill entity generation using Booking and Strategy Pattern
    @Override
    public Bill getDetailedBillEntity(int bookingId) throws SQLException {
        Booking booking = bookingDAO.getBookingEntityById(bookingId);
        if (booking == null) {
            throw new SQLException("Booking ID #" + bookingId + " not found.");
        }

        // Apply ExtendedStayBillingStrategy if stay >= 5 nights, otherwise StandardBillingStrategy
        BillingStrategy strategy = booking.getNumberOfNights() >= 5
            ? new ExtendedStayBillingStrategy()
            : new StandardBillingStrategy();

        return new Bill(booking, strategy);
    }

    // Finalize checkout and return strongly-typed Bill entity
    @Override
    public Bill checkoutWithBillEntity(int bookingId) throws SQLException {
        Bill bill = getDetailedBillEntity(bookingId);
        if (bill.getBooking().getStatus() != BookingStatus.BOOKED) {
            throw new SQLException("Booking ID #" + bookingId + " is already checked out.");
        }

        checkout(bookingId);
        bill.getBooking().setStatus(BookingStatus.CHECKED_OUT);
        bill.recalculate();
        return bill;
    }

    // Map representation for Web API (preserves full backward compatibility)
    public Map<String, Object> getDetailedBill(int bookingId) throws SQLException {
        Bill bill = getDetailedBillEntity(bookingId);
        return bill.toMap();
    }

    // Checkout with map representation for Web API
    public Map<String, Object> checkoutWithBill(int bookingId) throws SQLException {
        Bill bill = checkoutWithBillEntity(bookingId);
        return bill.toMap();
    }
}
