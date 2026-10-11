import java.util.Arrays;
import java.util.List;

/**
 * StandardRoom represents an entry-tier room (Single or Standard).
 * Demonstrates Inheritance and Polymorphic Method Overriding.
 */
public class StandardRoom extends Room {

    public StandardRoom(int roomId, String roomNumber, double pricePerNight, RoomStatus status) {
        super(roomId, roomNumber, "Single", pricePerNight, status);
    }

    public StandardRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, "Single", pricePerNight);
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Single Bed", "High-speed Wi-Fi 500Mbps", "Air Conditioning", "Work Desk");
    }

    @Override
    public int getMaxOccupancy() {
        return 1;
    }
}
