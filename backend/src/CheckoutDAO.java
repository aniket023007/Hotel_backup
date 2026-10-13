import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

// =============================================================================
// CHECKOUT DATA ACCESS INTERFACE
// =============================================================================

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
// CHECKOUT DATA ACCESS OBJECT (DAO Pattern Implementation)
// =============================================================================

/**
 * CheckoutDAO implements ICheckoutDAO.
 * Handles checkout transactions and bill generation.
 * Demonstrates DAO Pattern, Transaction Management, and Strategy Pattern-driven Billing.
 */
public class CheckoutDAO implements ICheckoutDAO {

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
