import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

// =============================================================================
// BOOKING STATUS ENUM
// =============================================================================

/**
 * BookingStatus enumeration representing the lifecycle state of a reservation.
 */
enum BookingStatus {
    BOOKED("Booked"),
    CHECKED_OUT("Checked Out"),
    CANCELLED("Cancelled");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BookingStatus fromString(String text) {
        if (text == null) return BOOKED;
        for (BookingStatus status : BookingStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return BOOKED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

// =============================================================================
// BOOKING DOMAIN ENTITY
// =============================================================================

/**
 * Booking domain entity.
 * Demonstrates Object Composition (HAS-A Guest, HAS-A Room), Encapsulation, and Business Logic.
 */
public class Booking implements Displayable {

    private int bookingId;
    private Guest guest;
    private Room room;
    private int numberOfNights;
    private String bookingDate;
    private BookingStatus status;

    // Overloaded Constructor 1 (Original constructor for backward compatibility)
    public Booking(int bookingId, Guest guest, Room room, int numberOfNights) {
        this(bookingId, guest, room, numberOfNights, "", BookingStatus.BOOKED);
    }

    // Overloaded Constructor 2 (Full constructor with date and status)
    public Booking(int bookingId, Guest guest, Room room, int numberOfNights, String bookingDate, BookingStatus status) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null.");
        }
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Nights must be greater than zero.");
        }

        this.bookingId = bookingId;
        this.guest = guest;
        this.room = room;
        this.numberOfNights = numberOfNights;
        this.bookingDate = bookingDate != null ? bookingDate : "";
        this.status = status != null ? status : BookingStatus.BOOKED;
    }

    // Encapsulation: Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        if (guest == null) throw new IllegalArgumentException("Guest cannot be null.");
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        if (room == null) throw new IllegalArgumentException("Room cannot be null.");
        this.room = room;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Nights must be greater than zero.");
        }
        this.numberOfNights = numberOfNights;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate != null ? bookingDate : "";
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status != null ? status : BookingStatus.BOOKED;
    }

    // Domain Calculation Logic
    public double calculateSubtotal() {
        return room.calculateTotalCost(numberOfNights);
    }

    public double calculateTax() {
        double subtotal = calculateSubtotal();
        return Math.round(subtotal * 0.12 * 100.0) / 100.0;
    }

    public double calculateTotalAmount() {
        return Math.round((calculateSubtotal() + calculateTax()) * 100.0) / 100.0;
    }

    // Displayable Implementation
    @Override
    public void displayDetails() {
        displayBookingDetails();
    }

    public void displayBookingDetails() {
        System.out.println("----- Booking Reservation #" + bookingId + " -----");
        System.out.println("Guest:            " + guest.getName() + " (Phone: " + guest.getPhone() + ")");
        System.out.println("Room:             #" + room.getRoomNumber() + " (" + room.getRoomType() + ")");
        System.out.println("Nights:           " + numberOfNights);
        System.out.println("Rate Per Night:   Rs. " + room.getPricePerNight());
        System.out.println("Subtotal:         Rs. " + calculateSubtotal());
        System.out.println("Total with Tax:   Rs. " + calculateTotalAmount());
        System.out.println("Status:           " + status);
        System.out.println("---------------------------------------------");
    }

    // Map representation for Web API compatibility
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("bookingId", bookingId);
        map.put("guestId", guest.getId());
        map.put("guestName", guest.getName());
        map.put("guestPhone", guest.getPhone());
        map.put("guestEmail", guest.getEmail());
        map.put("guestAddress", guest.getAddress());
        map.put("roomId", room.getRoomId());
        map.put("roomNumber", room.getRoomNumber());
        map.put("roomType", room.getRoomType());
        map.put("pricePerNight", room.getPricePerNight());
        map.put("numberOfNights", numberOfNights);
        map.put("subtotal", calculateSubtotal());
        map.put("tax", calculateTax());
        map.put("totalAmount", calculateTotalAmount());
        map.put("bookingDate", bookingDate);
        map.put("status", status.getDisplayName());
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking[id=" + bookingId + ", guest=" + guest.getName() + ", room=" + room.getRoomNumber() + ", nights=" + numberOfNights + ", status=" + status + "]";
    }
}
