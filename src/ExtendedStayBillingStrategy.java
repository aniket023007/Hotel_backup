/**
 * ExtendedStayBillingStrategy: 15% discount for stays of 5 or more nights.
 * Demonstrates Polymorphism and Strategy Pattern implementation.
 */
public class ExtendedStayBillingStrategy implements BillingStrategy {

    private static final double EXTENDED_DISCOUNT_RATE = 0.15; // 15% for >= 5 nights
    private static final double TAX_RATE = 0.12;               // 12% GST

    @Override
    public String getStrategyName() {
        return "Extended Stay (15% off for 5+ Nights + 12% GST)";
    }

    @Override
    public double calculateDiscount(double subtotal, int numberOfNights) {
        if (numberOfNights >= 5) {
            return Math.round(subtotal * EXTENDED_DISCOUNT_RATE * 100.0) / 100.0;
        }
        return 0.0;
    }

    @Override
    public double getTaxRate() {
        return TAX_RATE;
    }

    @Override
    public double calculateTax(double amountAfterDiscount) {
        return Math.round(amountAfterDiscount * TAX_RATE * 100.0) / 100.0;
    }

    @Override
    public double calculateGrandTotal(double subtotal, int numberOfNights) {
        double discount = calculateDiscount(subtotal, numberOfNights);
        double taxable = subtotal - discount;
        double tax = calculateTax(taxable);
        return Math.round((taxable + tax) * 100.0) / 100.0;
    }
}
