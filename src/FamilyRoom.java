import java.util.Arrays;
import java.util.List;

/**
 * FamilyRoom represents a multi-bed suite for families.
 * Demonstrates Inheritance and Polymorphic Method Overriding.
 */
public class FamilyRoom extends Room {

    public FamilyRoom(int roomId, String roomNumber, double pricePerNight, RoomStatus status) {
        super(roomId, roomNumber, "Family", pricePerNight, status);
    }

    public FamilyRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, "Family", pricePerNight);
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("2 Queen Beds", "Connecting Rooms", "Dining Area", "Kids Play Zone", "Kitchenette", "Smart TV 65\"");
    }

    @Override
    public int getMaxOccupancy() {
        return 5;
    }
}
