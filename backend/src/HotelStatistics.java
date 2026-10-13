import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HotelStatistics Value Object (DTO).
 * Demonstrates Encapsulation and Immutable Value Modeling for KPI reporting.
 */
public class HotelStatistics {

    private final int totalRooms;
    private final int availableRooms;
    private final int bookedRooms;
    private final int occupancyRate;
    private final int totalGuests;
    private final int activeBookings;
    private final int totalBookingsHistory;
    private final double totalRevenue;

    public HotelStatistics(int totalRooms, int availableRooms, int bookedRooms,
                           int occupancyRate, int totalGuests, int activeBookings,
                           int totalBookingsHistory, double totalRevenue) {
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.bookedRooms = bookedRooms;
        this.occupancyRate = occupancyRate;
        this.totalGuests = totalGuests;
        this.activeBookings = activeBookings;
        this.totalBookingsHistory = totalBookingsHistory;
        this.totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public int getBookedRooms() {
        return bookedRooms;
    }

    public int getOccupancyRate() {
        return occupancyRate;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public int getActiveBookings() {
        return activeBookings;
    }

    public int getTotalBookingsHistory() {
        return totalBookingsHistory;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalRooms", totalRooms);
        map.put("availableRooms", availableRooms);
        map.put("bookedRooms", bookedRooms);
        map.put("occupancyRate", occupancyRate);
        map.put("totalGuests", totalGuests);
        map.put("activeBookings", activeBookings);
        map.put("totalBookingsHistory", totalBookingsHistory);
        map.put("totalRevenue", totalRevenue);
        return map;
    }
}
