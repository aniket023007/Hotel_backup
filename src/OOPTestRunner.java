import java.util.List;

/**
 * OOPTestRunner validates that all OOP pillars, design patterns,
 * and system integrations function correctly.
 */
public class OOPTestRunner {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     HOTEL MANAGEMENT SYSTEM - OOP TEST RUNNER    ");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // 1. INHERITANCE & POLYMORPHISM (Person -> Guest, Staff)
        try {
            System.out.print("[TEST 1] Person Inheritance & Polymorphic displayDetails()... ");
            Person guest = new Guest(1, "Aniket Warkare", "+91 9876543210", "aniket@example.com", "Pune, India");
            Person staff = new Staff(2, "Rahul Sharma", "+91 9123456780", "rahul@hotel.com", "Senior Concierge", "Front Desk", 45000);

            if (!"Aniket Warkare".equals(guest.getName()) || !"Rahul Sharma".equals(staff.getName())) {
                throw new AssertionError("Person inheritance getters failed");
            }
            passed++;
            System.out.println("PASSED");
        } catch (Exception e) {
            failed++;
            System.out.println("FAILED: " + e.getMessage());
        }

        // 2. ROOM HIERARCHY, POLYMORPHISM & FACTORY PATTERN
        try {
            System.out.print("[TEST 2] Room Hierarchy & Factory Pattern (RoomFactory)... ");
            Room single = RoomFactory.createRoom(101, "101", "Single", 1500, RoomStatus.AVAILABLE);
            Room deluxe = RoomFactory.createRoom(201, "201", "Deluxe", 3500, RoomStatus.AVAILABLE);
            Room suite = RoomFactory.createRoom(301, "301", "Suite", 7500, RoomStatus.AVAILABLE);
            Room family = RoomFactory.createRoom(401, "401", "Family", 5000, RoomStatus.AVAILABLE);

            if (!(single instanceof StandardRoom) || !(deluxe instanceof DeluxeRoom) ||
                !(suite instanceof SuiteRoom) || !(family instanceof FamilyRoom)) {
                throw new AssertionError("RoomFactory did not instantiate correct polymorphic subclasses");
            }

            if (single.getMaxOccupancy() != 1 || deluxe.getMaxOccupancy() != 2 ||
                suite.getMaxOccupancy() != 4 || family.getMaxOccupancy() != 5) {
                throw new AssertionError("Polymorphic getMaxOccupancy() failed");
            }
            passed++;
            System.out.println("PASSED");
        } catch (Exception e) {
            failed++;
            System.out.println("FAILED: " + e.getMessage());
        }

        // 3. BOOKABLE & BILLABLE INTERFACE CONTRACTS
        try {
            System.out.print("[TEST 3] Bookable & Billable Interface Contracts... ");
            Room suite = RoomFactory.createRoom(501, "501", "Suite", 10000, RoomStatus.AVAILABLE);
            if (!suite.isAvailable()) throw new AssertionError("Initial status should be available");

            suite.bookRoom();
            if (suite.isAvailable() || suite.getStatus() != RoomStatus.BOOKED) {
                throw new AssertionError("Room should be booked after bookRoom()");
            }

            suite.checkOut();
            if (!suite.isAvailable() || suite.getStatus() != RoomStatus.AVAILABLE) {
                throw new AssertionError("Room should be available after checkOut()");
            }

            double cost3Nights = suite.calculateTotalCost(3);
            if (cost3Nights != 30000.0) {
                throw new AssertionError("Billable calculateTotalCost failed: expected 30000.0, got " + cost3Nights);
            }
            passed++;
            System.out.println("PASSED");
        } catch (Exception e) {
            failed++;
            System.out.println("FAILED: " + e.getMessage());
        }

        // 4. STRATEGY PATTERN (BillingStrategy implementations)
        try {
            System.out.print("[TEST 4] Strategy Pattern (Standard, Corporate, ExtendedStay)... ");
            Guest g = new Guest(1, "Vikram Malhotra", "+91 9998887776", "vikram@corp.com", "Mumbai");
            Room r = RoomFactory.createRoom(102, "102", "Deluxe", 2000, RoomStatus.AVAILABLE);
            Booking bookingShort = new Booking(901, g, r, 2); // 2 nights -> subtotal = 4000
            Booking bookingLong = new Booking(902, g, r, 6);  // 6 nights -> subtotal = 12000

            // Standard Strategy: 4000 subtotal, 0 discount, 12% tax = 480, total = 4480
            Bill standardBill = new Bill(bookingShort, new StandardBillingStrategy());
            if (standardBill.getGrandTotal() != 4480.0) {
                throw new AssertionError("StandardBillingStrategy total mismatch: " + standardBill.getGrandTotal());
            }

            // Corporate Strategy: 10% discount on 4000 = 400, taxable = 3600, 12% tax = 432, total = 4032
            Bill corporateBill = new Bill(bookingShort, new CorporateBillingStrategy());
            if (corporateBill.getDiscount() != 400.0 || corporateBill.getGrandTotal() != 4032.0) {
                throw new AssertionError("CorporateBillingStrategy total mismatch: " + corporateBill.getGrandTotal());
            }

            // Extended Stay Strategy: 15% discount on 12000 = 1800, taxable = 10200, 12% tax = 1224, total = 11424
            Bill extendedBill = new Bill(bookingLong, new ExtendedStayBillingStrategy());
            if (extendedBill.getDiscount() != 1800.0 || extendedBill.getGrandTotal() != 11424.0) {
                throw new AssertionError("ExtendedStayBillingStrategy total mismatch: " + extendedBill.getGrandTotal());
            }
            passed++;
            System.out.println("PASSED");
        } catch (Exception e) {
            failed++;
            System.out.println("FAILED: " + e.getMessage());
        }

        // 5. SERVICE LAYER & DATABASE INTEGRATION
        try {
            System.out.print("[TEST 5] Service Layer Integration with Live MySQL Database... ");
            IHotelService service = new HotelService();
            HotelStatistics stats = service.getHotelStatistics();

            if (stats.getTotalRooms() < 0 || stats.getTotalGuests() < 0) {
                throw new AssertionError("Invalid stats returned from database");
            }

            List<Room> rooms = service.getAllRooms();
            if (rooms == null) throw new AssertionError("Rooms list cannot be null");

            System.out.println("PASSED (Rooms in DB: " + rooms.size() + ", Occupancy: " + stats.getOccupancyRate() + "%)");
            passed++;
        } catch (Exception e) {
            failed++;
            System.out.println("FAILED: " + e.getMessage());
        }

        System.out.println("==================================================");
        System.out.println("TEST SUMMARY: " + passed + " Passed, " + failed + " Failed");
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
