import java.sql.SQLException;
import java.util.List;

/**
 * IHotelService interface defining high-level business workflow operations.
 * Demonstrates Service Layer Pattern, Abstraction, and Dependency Inversion Principle (DIP).
 */
public interface IHotelService {

    // Guest operations
    int registerGuest(Guest guest) throws SQLException;
    List<Guest> getAllGuests() throws SQLException;
    Guest getGuestById(int guestId) throws SQLException;

    // Room operations
    void addRoom(Room room) throws SQLException;
    List<Room> getAllRooms() throws SQLException;
    Room getRoomById(int roomId) throws SQLException;

    // Booking operations
    Booking bookRoom(int bookingId, int guestId, int roomId, int numberOfNights) throws SQLException;
    Booking bookRoom(Booking booking) throws SQLException;
    List<Booking> getActiveBookings() throws SQLException;
    List<Booking> getAllBookingsHistory() throws SQLException;
    Booking getBookingById(int bookingId) throws SQLException;
    int getNextBookingId() throws SQLException;

    // Checkout & Billing operations
    Bill calculateBill(int bookingId) throws SQLException;
    Bill processCheckout(int bookingId) throws SQLException;

    // Hotel Performance Metrics
    HotelStatistics getHotelStatistics() throws SQLException;
}
