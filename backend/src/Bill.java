import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bill entity representing a finalized or preview invoice.
 * Demonstrates Object Composition (HAS-A Booking, HAS-A BillingStrategy),
 * Strategy Pattern integration, and Encapsulation.
 */
public class Bill implements Displayable {

    private Booking booking;
    private BillingStrategy strategy;
    private double subtotal;
    private double discount;
    private double taxRate;
    private double taxAmount;
    private double grandTotal;

    // Overloaded Constructor 1 (Backward compatible with original 1-arg constructor)
    public Bill(Booking booking) {
        this(booking, new StandardBillingStrategy());
    }

    // Overloaded Constructor 2 (Strategy-aware constructor)
    public Bill(Booking booking, BillingStrategy strategy) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        this.booking = booking;
        this.strategy = strategy != null ? strategy : new StandardBillingStrategy();
        recalculate();
    }

    public void setStrategy(BillingStrategy strategy) {
        this.strategy = strategy != null ? strategy : new StandardBillingStrategy();
        recalculate();
    }

    public void recalculate() {
        this.subtotal = booking.calculateSubtotal();
        this.discount = strategy.calculateDiscount(subtotal, booking.getNumberOfNights());
        double taxable = this.subtotal - this.discount;
        this.taxRate = strategy.getTaxRate() * 100.0;
        this.taxAmount = strategy.calculateTax(taxable);
        this.grandTotal = strategy.calculateGrandTotal(subtotal, booking.getNumberOfNights());
    }

    // Encapsulation: Getters
    public Booking getBooking() {
        return booking;
    }

    public BillingStrategy getStrategy() {
        return strategy;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    // Backward compatible total calculation
    public double calculateTotal() {
        return grandTotal;
    }

    // Displayable Implementation
    @Override
    public void displayDetails() {
        displayBill();
    }

    public void displayBill() {
        System.out.println("==================================================");
        System.out.println("            GRAND HORIZON - HOTEL FOLIO           ");
        System.out.println("==================================================");
        System.out.println("Invoice For:    " + booking.getGuest().getName());
        System.out.println("Contact:        " + booking.getGuest().getPhone());
        System.out.println("Room:           #" + booking.getRoom().getRoomNumber() + " (" + booking.getRoom().getRoomType() + ")");
        System.out.println("Nights Stayed:  " + booking.getNumberOfNights());
        System.out.println("Rate Per Night: Rs. " + booking.getRoom().getPricePerNight());
        System.out.println("Tariff Scheme:  " + strategy.getStrategyName());
        System.out.println("--------------------------------------------------");
        System.out.println("Subtotal:       Rs. " + subtotal);
        if (discount > 0) {
            System.out.println("Discount:      -Rs. " + discount);
        }
        System.out.println("Tax (" + (int) taxRate + "% GST):    Rs. " + taxAmount);
        System.out.println("==================================================");
        System.out.println("Grand Total:    Rs. " + grandTotal);
        System.out.println("Status:         " + booking.getStatus());
        System.out.println("==================================================");
    }

    // Map representation for Web API & Invoice Modal compatibility
    public Map<String, Object> toMap() {
        Map<String, Object> bill = new LinkedHashMap<>();
        bill.put("bookingId", booking.getBookingId());
        bill.put("guestId", booking.getGuest().getId());
        bill.put("guestName", booking.getGuest().getName());
        bill.put("guestPhone", booking.getGuest().getPhone());
        bill.put("guestEmail", booking.getGuest().getEmail());
        bill.put("guestAddress", booking.getGuest().getAddress());
        bill.put("roomId", booking.getRoom().getRoomId());
        bill.put("roomNumber", booking.getRoom().getRoomNumber());
        bill.put("roomType", booking.getRoom().getRoomType());
        bill.put("pricePerNight", booking.getRoom().getPricePerNight());
        bill.put("numberOfNights", booking.getNumberOfNights());
        bill.put("subtotal", subtotal);
        bill.put("discount", discount);
        bill.put("taxRate", (int) taxRate);
        bill.put("taxAmount", taxAmount);
        bill.put("grandTotal", grandTotal);
        bill.put("bookingDate", booking.getBookingDate());
        bill.put("status", booking.getStatus().getDisplayName());
        bill.put("strategyName", strategy.getStrategyName());
        return bill;
    }
}
