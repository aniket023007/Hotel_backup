/**
 * BillingStrategy interface implementing the Strategy Design Pattern.
 * Demonstrates Abstraction and Runtime Polymorphism in financial calculation.
 */
public interface BillingStrategy {
    String getStrategyName();
    double calculateDiscount(double subtotal, int numberOfNights);
    double getTaxRate();
    double calculateTax(double amountAfterDiscount);
    double calculateGrandTotal(double subtotal, int numberOfNights);
}

/**
 * StandardBillingStrategy: Default standard billing with 12% GST tax.
 * Demonstrates Polymorphism and Strategy Pattern implementation.
 */
class StandardBillingStrategy implements BillingStrategy {

    private static final double TAX_RATE = 0.12; // 12% GST

    @Override
    public String getStrategyName() {
        return "Standard Tariff (12% GST)";
    }

    @Override
    public double calculateDiscount(double subtotal, int numberOfNights) {
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

/**
 * CorporateBillingStrategy: Offers 10% corporate discount on room tariff + 12% GST.
 * Demonstrates Polymorphism and Strategy Pattern implementation.
 */
class CorporateBillingStrategy implements BillingStrategy {

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

/**
 * ExtendedStayBillingStrategy: 15% discount for stays of 5 or more nights.
 * Demonstrates Polymorphism and Strategy Pattern implementation.
 */
class ExtendedStayBillingStrategy implements BillingStrategy {

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
