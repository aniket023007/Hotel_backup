import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * =============================================================================
 * ROOM DOMAIN HIERARCHY & FACTORY PATTERN
 * Demonstrates:
 * - Abstraction: Abstract Room class + Bookable, Billable, Displayable interfaces
 * - Inheritance: StandardRoom, DeluxeRoom, SuiteRoom, FamilyRoom extend Room
 * - Polymorphism: Overridden getAmenities(), getMaxOccupancy(), displayDetails()
 * - Encapsulation: Private state (price, status) with guarded mutations
 * - Factory Method Pattern: RoomFactory instantiates subclasses dynamically
 * =============================================================================
 */

// =============================================================================
// INTERFACES (Behavioral Contracts)
// =============================================================================

interface Displayable {
    void displayDetails();
}

interface Bookable {
    boolean isAvailable();
    void bookRoom();
    void checkOut();
}

interface Billable {
    double calculateTotalCost(int numberOfNights);
}

// =============================================================================
// ROOM STATUS ENUM
// =============================================================================

enum RoomStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoomStatus fromString(String text) {
        if (text == null) return AVAILABLE;
        for (RoomStatus status : RoomStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return AVAILABLE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

// =============================================================================
// ABSTRACT BASE ROOM CLASS
// =============================================================================

public abstract class Room implements Bookable, Billable, Displayable {

    private int roomId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private RoomStatus status;

    public Room(int roomNumber, String roomType, double pricePerNight) {
        this(roomNumber, String.valueOf(roomNumber), roomType, pricePerNight, RoomStatus.AVAILABLE);
    }

    public Room(int roomId, String roomNumber, String roomType, double pricePerNight, RoomStatus status) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty.");
        }
        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price per night cannot be negative.");
        }
        this.roomId = roomId;
        this.roomNumber = roomNumber.trim();
        this.roomType = (roomType != null && !roomType.trim().isEmpty()) ? roomType.trim() : "Standard";
        this.pricePerNight = pricePerNight;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
    }

    // Encapsulation: Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty.");
        }
        this.roomNumber = roomNumber.trim();
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price per night cannot be negative.");
        }
        this.pricePerNight = pricePerNight;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status != null ? status : RoomStatus.AVAILABLE;
    }

    // Interface Implementation: Bookable
    @Override
    public boolean isAvailable() {
        return this.status == RoomStatus.AVAILABLE;
    }

    @Override
    public void bookRoom() {
        if (isAvailable()) {
            this.status = RoomStatus.BOOKED;
            System.out.println("Room #" + roomNumber + " booked successfully!");
        } else {
            System.out.println("Room #" + roomNumber + " is already occupied!");
        }
    }

    @Override
    public void checkOut() {
        this.status = RoomStatus.AVAILABLE;
        System.out.println("Checkout completed. Room #" + roomNumber + " is now available.");
    }

    // Abstract methods to be polymorphically implemented by specific room tiers
    public abstract List<String> getAmenities();
    public abstract int getMaxOccupancy();

    // Interface Implementation: Billable
    @Override
    public double calculateTotalCost(int numberOfNights) {
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than zero.");
        }
        return numberOfNights * pricePerNight;
    }

    // Interface Implementation: Displayable
    @Override
    public void displayDetails() {
        displayRoomDetails();
    }

    public void displayRoomDetails() {
        System.out.println("----- Room Details -----");
        System.out.println("Room Number:     " + roomNumber);
        System.out.println("Room Type:       " + roomType);
        System.out.println("Price Per Night: Rs. " + pricePerNight);
        System.out.println("Status:          " + status);
        System.out.println("Max Occupancy:   " + getMaxOccupancy() + " guests");
        System.out.println("Amenities:       " + String.join(", ", getAmenities()));
        System.out.println("------------------------");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("roomId", roomId);
        map.put("roomNumber", roomNumber);
        map.put("roomType", roomType);
        map.put("pricePerNight", pricePerNight);
        map.put("status", status.getDisplayName());
        map.put("maxOccupancy", getMaxOccupancy());
        map.put("amenities", getAmenities());
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomId == room.roomId && Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, roomNumber);
    }

    @Override
    public String toString() {
        return "Room[id=" + roomId + ", number='" + roomNumber + "', type='" + roomType + "', price=" + pricePerNight + ", status=" + status + "]";
    }
}

// =============================================================================
// CONCRETE POLYMORPHIC SUBCLASSES
// =============================================================================

class StandardRoom extends Room {

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

class DeluxeRoom extends Room {

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

class SuiteRoom extends Room {

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

class FamilyRoom extends Room {

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

// =============================================================================
// FACTORY METHOD PATTERN
// =============================================================================

class RoomFactory {

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
