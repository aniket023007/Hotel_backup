import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * HotelService implements IHotelService.
 * Orchestrates business workflows across Domain Models and DAO contracts.
 * Demonstrates Service Layer Pattern, Dependency Injection, and Separation of Concerns.
 */
public class HotelService implements IHotelService {

    private final IGuestDAO guestDAO;
    private final IRoomDAO roomDAO;
    private final IBookingDAO bookingDAO;
    private final ICheckoutDAO checkoutDAO;

    // Optional in-memory cache lists for legacy console support
    private final List<Guest> inMemoryGuests = new ArrayList<>();
    private final List<Room> inMemoryRooms = new ArrayList<>();
    private final List<Booking> inMemoryBookings = new ArrayList<>();

    // Default Constructor: Uses standard DAO implementations
    public HotelService() {
        this(new GuestDAO(), new RoomDAO(), new BookingDAO(), new CheckoutDAO());
    }

    // Dependency Injection Constructor: Enables loose coupling and testing
    public HotelService(IGuestDAO guestDAO, IRoomDAO roomDAO, IBookingDAO bookingDAO, ICheckoutDAO checkoutDAO) {
        this.guestDAO = guestDAO;
        this.roomDAO = roomDAO;
        this.bookingDAO = bookingDAO;
        this.checkoutDAO = checkoutDAO;
    }

    // =========================================================================
    // GUEST WORKFLOWS
    // =========================================================================

    @Override
    public int registerGuest(Guest guest) throws SQLException {
        if (guest == null) {
            throw new IllegalArgumentException("Guest entity cannot be null.");
        }
        if (guest.getName() == null || guest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Guest full name is required.");
        }
        int id = guestDAO.saveGuest(guest);
        inMemoryGuests.add(guest);
        return id;
    }

    @Override
    public List<Guest> getAllGuests() throws SQLException {
        return guestDAO.getAllGuestEntities();
    }

    @Override
    public Guest getGuestById(int guestId) throws SQLException {
        return guestDAO.getGuestEntityById(guestId);
    }

    // =========================================================================
    // ROOM WORKFLOWS
    // =========================================================================

    @Override
    public void addRoom(Room room) throws SQLException {
        if (room == null) {
            throw new IllegalArgumentException("Room entity cannot be null.");
        }
        roomDAO.addRoom(room);
        inMemoryRooms.add(room);
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRoomEntities();
    }

    @Override
    public Room getRoomById(int roomId) throws SQLException {
        return roomDAO.getRoomEntityById(roomId);
    }

    // =========================================================================
    // BOOKING WORKFLOWS
    // =========================================================================

    @Override
    public Booking bookRoom(int bookingId, int guestId, int roomId, int numberOfNights) throws SQLException {
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than zero.");
        }

        Room room = roomDAO.getRoomEntityById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room #" + roomId + " does not exist.");
        }
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room #" + room.getRoomNumber() + " is currently occupied.");
        }

        Guest guest = guestDAO.getGuestEntityById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest #" + guestId + " is not registered.");
        }

        bookingDAO.bookRoom(bookingId, guestId, roomId, numberOfNights);

        return new Booking(bookingId, guest, room, numberOfNights);
    }

    @Override
    public Booking bookRoom(Booking booking) throws SQLException {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        return bookRoom(
            booking.getBookingId(),
            booking.getGuest().getId(),
            booking.getRoom().getRoomId(),
            booking.getNumberOfNights()
        );
    }

    @Override
    public List<Booking> getActiveBookings() throws SQLException {
        return bookingDAO.getAllBookingEntities(true);
    }

    @Override
    public List<Booking> getAllBookingsHistory() throws SQLException {
        return bookingDAO.getAllBookingEntities(false);
    }

    @Override
    public Booking getBookingById(int bookingId) throws SQLException {
        return bookingDAO.getBookingEntityById(bookingId);
    }

    @Override
    public int getNextBookingId() throws SQLException {
        return bookingDAO.getNextBookingId();
    }

    // =========================================================================
    // BILLING & CHECKOUT WORKFLOWS
    // =========================================================================

    @Override
    public Bill calculateBill(int bookingId) throws SQLException {
        return checkoutDAO.getDetailedBillEntity(bookingId);
    }

    @Override
    public Bill processCheckout(int bookingId) throws SQLException {
        return checkoutDAO.checkoutWithBillEntity(bookingId);
    }

    // =========================================================================
    // HOTEL METRICS & KPIS
    // =========================================================================

    @Override
    public HotelStatistics getHotelStatistics() throws SQLException {
        List<Room> rooms = roomDAO.getAllRoomEntities();
        List<Guest> guests = guestDAO.getAllGuestEntities();
        List<Booking> activeBookings = bookingDAO.getAllBookingEntities(true);
        List<Booking> allBookings = bookingDAO.getAllBookingEntities(false);

        int totalRooms = rooms.size();
        int availableRooms = 0;
        int bookedRooms = 0;

        for (Room r : rooms) {
            if (r.isAvailable()) {
                availableRooms++;
            } else {
                bookedRooms++;
            }
        }

        double totalRevenue = 0.0;
        for (Booking b : allBookings) {
            totalRevenue += b.calculateTotalAmount();
        }

        int occupancyRate = totalRooms > 0 ? (int) Math.round(((double) bookedRooms / totalRooms) * 100.0) : 0;

        return new HotelStatistics(
            totalRooms,
            availableRooms,
            bookedRooms,
            occupancyRate,
            guests.size(),
            activeBookings.size(),
            allBookings.size(),
            totalRevenue
        );
    }

    // =========================================================================
    // LEGACY METHODS (For backward compatibility)
    // =========================================================================

    public void addGuest(Guest guest) {
        inMemoryGuests.add(guest);
        System.out.println("Guest added successfully!");
    }

    public void displayRooms() {
        System.out.println("\n===== ROOM LIST =====");
        for (Room room : inMemoryRooms) {
            room.displayRoomDetails();
            System.out.println("---------------------");
        }
    }

    public void displayBookings() {
        System.out.println("\n===== BOOKING LIST =====");
        for (Booking booking : inMemoryBookings) {
            booking.displayBookingDetails();
        }
    }

    public void checkOut(int bookingId) {
        for (int i = 0; i < inMemoryBookings.size(); i++) {
            Booking b = inMemoryBookings.get(i);
            if (b.getBookingId() == bookingId) {
                Bill bill = new Bill(b);
                bill.displayBill();
                b.getRoom().checkOut();
                inMemoryBookings.remove(i);
                System.out.println("Checkout successful!");
                return;
            }
        }
        throw new IllegalArgumentException("Booking not found!");
    }
}
