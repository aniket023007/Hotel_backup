import java.util.Arrays;
import java.util.List;

/**
 * SuiteRoom represents a premium luxury suite.
 * Demonstrates Inheritance and Polymorphic Method Overriding.
 */
public class SuiteRoom extends Room {

    public SuiteRoom(int roomId, String roomNumber, double pricePerNight, RoomStatus status) {
        super(roomId, roomNumber, "Suite", pricePerNight, status);
    }

    public SuiteRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, "Suite", pricePerNight);
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Master Suite", "Separate Living Area", "Panoramic Skyline View", "Jacuzzi Spa", "Butler Service", "Complimentary Minibar");
    }

    @Override
    public int getMaxOccupancy() {
        return 4;
    }
}
