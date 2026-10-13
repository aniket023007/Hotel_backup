import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract Room base class.
 * Demonstrates Abstraction, Encapsulation, and Interface Implementation (Bookable, Billable, Displayable).
 */
public abstract class Room implements Bookable, Billable, Displayable {

    private int roomId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private RoomStatus status;

    // Overloaded Constructor 1 (Backward compatible with original 3-arg constructor)
    public Room(int roomNumber, String roomType, double pricePerNight) {
        this(roomNumber, String.valueOf(roomNumber), roomType, pricePerNight, RoomStatus.AVAILABLE);
    }

    // Overloaded Constructor 2 (Full domain constructor)
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

    // Map representation for Web API compatibility
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
