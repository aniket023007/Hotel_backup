/**
 * RoomFactory implements the Factory Method Design Pattern.
 * Encapsulates the instantiation of polymorphic Room subclasses based on category/type.
 */
public class RoomFactory {

    public static Room createRoom(int roomId, String roomNumber, String roomType, double pricePerNight, RoomStatus status) {
        if (roomType == null) {
            return new StandardRoom(roomId, roomNumber, pricePerNight, status);
        }

        String typeLower = roomType.trim().toLowerCase();
        if (typeLower.contains("suite")) {
            return new SuiteRoom(roomId, roomNumber, pricePerNight, status);
        } else if (typeLower.contains("family")) {
            return new FamilyRoom(roomId, roomNumber, pricePerNight, status);
        } else if (typeLower.contains("deluxe") || typeLower.contains("double")) {
            return new DeluxeRoom(roomId, roomNumber, roomType, pricePerNight, status);
        } else {
            return new StandardRoom(roomId, roomNumber, pricePerNight, status);
        }
    }

    public static Room createRoom(String roomNumber, String roomType, double pricePerNight) {
        return createRoom(0, roomNumber, roomType, pricePerNight, RoomStatus.AVAILABLE);
    }
}
