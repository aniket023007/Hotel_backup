import java.util.Arrays;
import java.util.List;

/**
 * DeluxeRoom represents an upgraded tier (Deluxe or Double).
 * Demonstrates Inheritance and Polymorphic Method Overriding.
 */
public class DeluxeRoom extends Room {

    public DeluxeRoom(int roomId, String roomNumber, String roomType, double pricePerNight, RoomStatus status) {
        super(roomId, roomNumber, (roomType != null && !roomType.trim().isEmpty()) ? roomType : "Deluxe", pricePerNight, status);
    }

    public DeluxeRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, "Deluxe", pricePerNight);
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("King/Queen Bed", "High-speed Wi-Fi 500Mbps", "Balcony", "Smart TV 55\"", "En-suite Tub", "Mini Fridge");
    }

    @Override
    public int getMaxOccupancy() {
        return 2;
    }
}
