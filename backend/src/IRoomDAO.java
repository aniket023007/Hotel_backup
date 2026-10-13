import java.sql.SQLException;
import java.util.List;

/**
 * IRoomDAO interface defining the data access contract for Room entities.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
public interface IRoomDAO {
    List<Room> getAllRoomEntities() throws SQLException;
    Room getRoomEntityById(int roomId) throws SQLException;
    void addRoom(Room room) throws SQLException;
    void addRoom(String roomNumber, String roomType, double pricePerNight) throws SQLException;
    void displayRooms() throws SQLException;
}
