import java.sql.SQLException;
import java.util.List;

/**
 * IGuestDAO interface defining the data access contract for Guest entities.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
public interface IGuestDAO {
    void addGuest(String fullName, String phone, String email, String address) throws SQLException;
    int addGuestAndGetId(String fullName, String phone, String email, String address) throws SQLException;
    int saveGuest(Guest guest) throws SQLException;
    List<Guest> getAllGuestEntities() throws SQLException;
    Guest getGuestEntityById(int guestId) throws SQLException;
    void displayGuests() throws SQLException;
}
