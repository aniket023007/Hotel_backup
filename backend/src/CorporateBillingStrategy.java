/**
 * CorporateBillingStrategy: Offers 10% corporate discount on room tariff + 12% GST.
 * Demonstrates Polymorphism and Strategy Pattern implementation.
 */
public class CorporateBillingStrategy implements BillingStrategy {

    private static final double DISCOUNT_RATE = 0.10; // 10% Corporate Discount
    private static final double TAX_RATE = 0.12;       // 12% GST

    @Override
    public String getStrategyName() {
        return "Corporate Partner (10% Discount + 12% GST)";
    }

    @Override
    public double calculateDiscount(double subtotal, int numberOfNights) {
        return Math.round(subtotal * DISCOUNT_RATE * 100.0) / 100.0;
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
