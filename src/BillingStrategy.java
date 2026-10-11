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
