import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * IBookingDAO interface defining the data access contract for Booking entities.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
public interface IBookingDAO {
    void bookRoom(int bookingId, int guestId, int roomId, int numberOfNights) throws SQLException;
    void bookRoom(Booking booking) throws SQLException;
    List<Booking> getAllBookingEntities(Boolean activeOnly) throws SQLException;
    Booking getBookingEntityById(int bookingId) throws SQLException;
    int getNextBookingId() throws SQLException;
    DefaultTableModel getBookingsTableModel() throws SQLException;
}
